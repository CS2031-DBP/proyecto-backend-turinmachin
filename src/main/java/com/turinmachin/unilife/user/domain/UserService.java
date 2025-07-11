package com.turinmachin.unilife.user.domain;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.turinmachin.unilife.authentication.domain.AuthProvider;
import com.turinmachin.unilife.authentication.event.ResetPasswordIssuedEvent;
import com.turinmachin.unilife.authentication.exception.AuthProviderNotCredentialsException;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.common.exception.UnauthorizedException;
import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.common.utils.HashUtils;
import com.turinmachin.unilife.common.utils.StringUtils;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.email.EmailUtils;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.exception.UniversityWithoutDegreeException;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserPasswordDto;
import com.turinmachin.unilife.user.event.SendVerificationEmailEvent;
import com.turinmachin.unilife.user.event.SendWelcomeEmailEvent;
import com.turinmachin.unilife.user.exception.EmailConflictException;
import com.turinmachin.unilife.user.exception.OnlyAdminException;
import com.turinmachin.unilife.user.exception.UserNotVerifiedException;
import com.turinmachin.unilife.user.exception.UserWithoutUniversityException;
import com.turinmachin.unilife.user.exception.UsernameConflictException;
import com.turinmachin.unilife.user.exception.VerificationEmailCooldownException;
import com.turinmachin.unilife.user.infrastructure.UserRepository;
import com.turinmachin.unilife.user.infrastructure.UserTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${cooldown.verification-resend}")
    private Duration verificationEmailCooldown;

    @Value("${auth.password-reset-duration}")
    private Duration passwordResetDuration;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final UniversityService universityService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileInfoService fileInfoService;
    private final ApplicationEventPublisher eventPublisher;

    public Page<User> getUsers(final Pageable pageable) {
        return userRepository.findAllByOrderByUsername(pageable);
    }

    public Page<User> getUsersExcluding(final UUID excludedId, final Pageable pageable) {
        return userRepository.findByIdNotOrderByUsername(excludedId, pageable);
    }

    public Page<User> searchUsersExcluding(final String query, final UUID excludedId, final Pageable pageable) {
        return userRepository.searchExcluding(query, excludedId, pageable);
    }

    public Optional<User> getUserById(final UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByUsernameOrEmail(final String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    public Optional<User> getUserByPasswordResetToken(final String token) {
        final String hashedToken = HashUtils.hashTokenSHA256(token);
        final Instant now = Instant.now();
        return userRepository.findByPasswordResetTokenValueAndPasswordResetTokenCreatedAtGreaterThan(hashedToken,
                now.minus(passwordResetDuration));
    }

    public Optional<User> getUserByVerificationId(final UUID verificationId) {
        return userRepository.findByVerificationId(verificationId);
    }

    public boolean userExistsByRole(final Role role) {
        return userRepository.existsByRole(role);
    }

    @Transactional
    public User createUser(final RegisterUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new EmailConflictException();

        if (userRepository.existsByUsername(dto.getUsername()))
            throw new UsernameConflictException();

        final User user = modelMapper.map(dto, User.class);
        user.setAuthProvider(AuthProvider.CREDENTIALS);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationId(UUID.randomUUID());
        return userRepository.save(user);
    }

    public User createGoogleUser(final String email, final Payload payload) {
        if (userRepository.existsByEmail(email))
            throw new EmailConflictException();

        final String nameSource = Optional.ofNullable(payload.get("given_name"))
                .orElseGet(() -> payload.get("name"))
                .toString();
        final String username = generateValidUsernameFrom(nameSource);

        final User user = new User();
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setEmail(email);
        user.setUsername(username);
        tryAssignGooglePictureToUser(user, payload);

        return assignUserToBelongingUniversity(user);
    }

    public User tryAssignGooglePictureToUser(User user, final Payload payload) {
        final String pictureUrl = payload.get("picture").toString();

        if (pictureUrl != null) {
            try {
                user = assignPictureToUserFromUrl(user, pictureUrl);
            } catch (final Exception ex) {
            }
        }

        return userRepository.save(user);
    }

    public User assignPictureToUserFromUrl(final User user, final String pictureUrl) throws IOException {
        final var pictureResponse = restTemplate.getForEntity(pictureUrl, Resource.class);

        if (pictureResponse.getStatusCode().is2xxSuccessful() && pictureResponse.hasBody()) {
            final String contentType = pictureResponse.getHeaders().getContentType().toString();

            final FileInfo profilePicture = fileInfoService.createFileUnchecked(
                    pictureResponse.getBody().getInputStream(),
                    user.getUsername() + "_profile_picture",
                    contentType);

            user.setProfilePicture(profilePicture);
        }

        return userRepository.save(user);
    }

    public String generateValidUsernameFrom(final String name) {
        String username = StringUtils.removeAccents(name.trim().replaceAll("\\s+", "_"))
                .replaceAll("[^a-zA-Z0-9.\\-_]", "");
        username = username.substring(0, Math.min(username.length(), 16));

        if (userRepository.existsByUsername(username)) {
            // PERF: could potentially cause an actual catastrophe (0.0001% chance)
            int suffix = 1;
            while (userRepository.existsByUsername(username + suffix)) {
                suffix++;
            }

            username += suffix;
        }

        return username;
    }

    @Transactional
    public User verifyUser(final User user) {
        user.setVerificationId(null);
        return assignUserToBelongingUniversity(user);
    }

    @Transactional
    public User assignUserToBelongingUniversity(final User user) {
        final String emailDomain = EmailUtils.extractDomain(user.getEmail());

        final Optional<University> university = universityService.getUniversityByEmailDomain(emailDomain);
        user.setUniversity(university.orElse(null));

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(final User user, final UpdateUserDto dto) {
        if (!Objects.equals(user.getUsername(), dto.getUsername())
                && userRepository.existsByUsername(dto.getUsername()))
            throw new UsernameConflictException();

        user.setUsername(dto.getUsername());
        user.setDisplayName(dto.getDisplayName());
        user.setBio(dto.getBio());

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (user.getAuthProvider() != AuthProvider.CREDENTIALS)
                throw new AuthProviderNotCredentialsException();

            // Update email
            user.setEmail(dto.getEmail());
            user.setVerificationId(UUID.randomUUID());
            user.setUniversity(null);
            user.setDegree(null);
            eventPublisher.publishEvent(new SendVerificationEmailEvent(user));
        } else {
            // Update degree
            final UUID userDegreeId = Optional.ofNullable(user.getDegree()).map(Degree::getId).orElse(null);

            if (!Objects.equals(dto.getDegreeId(), userDegreeId)) {
                if (dto.getDegreeId() == null) {
                    user.setDegree(null);
                } else if (user.getUniversity() == null) {
                    throw new UserWithoutUniversityException();
                } else {
                    final Degree newDegree = user.getUniversity().getDegrees()
                            .stream()
                            .filter(d -> d.getId().equals(dto.getDegreeId()))
                            .findFirst()
                            .orElseThrow(UniversityWithoutDegreeException::new);

                    user.setDegree(newDegree);
                }
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserPassword(final User user, final UpdateUserPasswordDto dto) {
        if (user.getPassword() != null && !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
            throw new UnauthorizedException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserRole(final User user, final Role role) {
        if (user.getRole() == Role.ADMIN && !userRepository.existsByRoleAndIdNot(Role.ADMIN, user.getId()))
            throw new OnlyAdminException();

        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(final User user) {
        if (user.getRole() == Role.ADMIN && !userRepository.existsByRoleAndIdNot(Role.ADMIN, user.getId()))
            throw new OnlyAdminException();

        userRepository.delete(user);
    }

    public void checkUserVerified(final User user) {
        if (!user.getVerified())
            throw new UserNotVerifiedException();

    }

    public int detachUniversity(final University university) {
        return userRepository.detachUniversity(university.getId());
    }

    public User updateUserProfilePicture(User user, final MultipartFile file) throws IOException {
        if (!fileInfoService.isContentTypeImage(file.getContentType()))
            throw new UnsupportedMediaTypeException();

        final FileInfo oldPicture = user.getProfilePicture();
        final FileInfo newPicture = fileInfoService.createFile(file);

        user.setProfilePicture(newPicture);
        user = userRepository.save(user);

        if (oldPicture != null) {
            fileInfoService.deleteFile(oldPicture);
        }
        return user;
    }

    public User deleteUserProfilePicture(User user) {
        final FileInfo oldPicture = user.getProfilePicture();

        if (oldPicture == null)
            throw new ConflictException("User does not have a profile picture");

        user.setProfilePicture(null);
        user = userRepository.save(user);
        fileInfoService.deleteFile(oldPicture);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Transactional
    public void syncUniversityAssociations(final University university) {
        final List<User> toDeassociate = userRepository.findAllByUniversity(university).stream()
                .filter(user -> !university.ownsEmail(user.getEmail()))
                .toList();

        for (final User user : toDeassociate) {
            user.setUniversity(null);
            user.setDegree(null);
        }

        userRepository.saveAll(toDeassociate);

        final List<User> toAssociate = userRepository.findAllByVerificationIdIsNullAndUniversityIsNull()
                .stream()
                .filter(user -> university.ownsEmail(user.getEmail()))
                .toList();

        for (final User user : toAssociate) {
            user.setUniversity(university);
        }

        userRepository.saveAll(toAssociate);
    }

    @Transactional
    public void removeInvalidDegrees(final University university) {
        final List<User> associatedUsers = userRepository.findAllByUniversity(university);
        final List<User> toSave = new ArrayList<>();

        for (final User user : associatedUsers) {
            if (user.getDegree() != null && !university.getDegrees().contains(user.getDegree())) {
                user.setDegree(null);
            }
        }

        userRepository.saveAll(toSave);
    }

    public void syncDegreeRemoval(final University university, final Degree degree) {
        userRepository.syncDegreeRemoval(university.getId(), degree.getId());
    }

    public void sendVerificationEmail(final User user) {
        final Instant now = Instant.now();

        if (user.getLastVerificationEmailSent() != null) {
            final Instant lastSent = user.getLastVerificationEmailSent();

            if (now.isBefore(lastSent.plus(verificationEmailCooldown)))
                throw new VerificationEmailCooldownException();
        }

        eventPublisher.publishEvent(new SendVerificationEmailEvent(user));

        user.setLastVerificationEmailSent(now);
        userRepository.save(user);
    }

    public User clearResetPasswordToken(final User user) {
        user.setPasswordResetToken(null);
        return userRepository.save(user);
    }

    public User setResetPasswordToken(User user, final String tokenValue) {
        user.setPasswordResetToken(null);
        userTokenRepository.deleteByUserId(user.getId());

        final UserToken token = new UserToken();
        token.setValue(HashUtils.hashTokenSHA256(tokenValue));
        token.setUser(user);
        user.setPasswordResetToken(token);

        user = userRepository.save(user);
        eventPublisher.publishEvent(new ResetPasswordIssuedEvent(user, tokenValue));
        return user;
    }

    public User resetPassword(final User user, final String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        return userRepository.save(user);
    }

    public boolean userTokenExistsByValue(final String value) {
        final String hashedToken = HashUtils.hashTokenSHA256(value);
        final Instant now = Instant.now();
        return userTokenRepository.existsByValueAndCreatedAtGreaterThan(hashedToken, now.minus(passwordResetDuration));
    }

    public boolean userHasValidToken(final User user) {
        final UserToken existingToken = user.getPasswordResetToken();

        if (existingToken == null)
            return false;

        final Instant now = Instant.now();
        return now.isBefore(existingToken.getCreatedAt().plus(passwordResetDuration));
    }

    public User upgradeUserAuthToGoogle(final User user, final Payload payload) {
        user.setAuthProvider(AuthProvider.GOOGLE);

        if (!user.getVerified()) {
            verifyUser(user);
            eventPublisher.publishEvent(new SendWelcomeEmailEvent(user));
        }

        if (user.getProfilePicture() == null) {
            tryAssignGooglePictureToUser(user, payload);
        }

        return userRepository.save(user);
    }

}

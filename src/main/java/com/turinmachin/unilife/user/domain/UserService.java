package com.turinmachin.unilife.user.domain;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.common.exception.UnauthorizedException;
import com.turinmachin.unilife.common.exception.UnsupportedMediaTypeException;
import com.turinmachin.unilife.degree.domain.Degree;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.fileinfo.domain.FileInfoService;
import com.turinmachin.unilife.university.domain.University;
import com.turinmachin.unilife.university.domain.UniversityService;
import com.turinmachin.unilife.university.exception.UniversityNotFoundException;
import com.turinmachin.unilife.university.exception.UniversityNotOwnEmailException;
import com.turinmachin.unilife.university.exception.UniversityWithoutDegreeException;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserPasswordDto;
import com.turinmachin.unilife.user.event.SendVerificationEmailEvent;
import com.turinmachin.unilife.user.exception.EmailConflictException;
import com.turinmachin.unilife.user.exception.OnlyAdminException;
import com.turinmachin.unilife.user.exception.UserNotVerifiedException;
import com.turinmachin.unilife.user.exception.UserWithoutUniversityException;
import com.turinmachin.unilife.user.exception.UsernameConflictException;
import com.turinmachin.unilife.user.infrastructure.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UniversityService universityService;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final FileInfoService fileInfoService;

    private final ApplicationEventPublisher eventPublisher;

    public Page<User> getAllUsersWithSpec(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    public Optional<User> getUserByVerificationId(UUID verificationId) {
        return userRepository.findByVerificationId(verificationId);
    }

    public boolean userExistsByRole(Role role) {
        return userRepository.existsByRole(role);
    }

    public User createUser(RegisterUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailConflictException();
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameConflictException();
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationId(UUID.randomUUID());
        return userRepository.save(user);
    }

    public User verifyUser(User user) {
        user.setVerificationId(null);
        return userRepository.save(user);
    }

    public User updateUser(User user, UpdateUserDto dto) {
        if (!Objects.equals(user.getUsername(), dto.getUsername())
                && userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameConflictException();
        }

        user.setUsername(dto.getUsername());
        user.setDisplayName(dto.getDisplayName());
        user.setBio(dto.getBio());

        UUID userUniversityId = Optional.ofNullable(user.getUniversity()).map(University::getId).orElse(null);

        if (!Objects.equals(dto.getUniversityId(), userUniversityId)) {
            Optional<University> newUniversity = Optional.ofNullable(dto.getUniversityId())
                    .map(id -> universityService.getActiveUniversityById(id)
                            .orElseThrow(UniversityNotFoundException::new));

            newUniversity.ifPresent(uni -> {
                if (!uni.ownsEmail(user.getEmail())) {
                    throw new UniversityNotOwnEmailException();
                }
            });

            user.setUniversity(newUniversity.orElse(null));
        }

        UUID userDegreeId = Optional.ofNullable(user.getDegree()).map(Degree::getId).orElse(null);

        if (!Objects.equals(dto.getDegreeId(), userDegreeId)) {
            Optional<Degree> newDegree = Optional
                    .ofNullable(dto.getDegreeId())
                    .map(degreeId -> Optional
                            .ofNullable(user.getUniversity())
                            .orElseThrow(UserWithoutUniversityException::new)
                            .getDegrees()
                            .stream()
                            .filter(d -> d.getId().equals(dto.getDegreeId()))
                            .findFirst()
                            .orElseThrow(UniversityWithoutDegreeException::new));

            user.setDegree(newDegree.orElse(null));
        }

        return userRepository.save(user);
    }

    public User updateUserEmail(User user, String email) {
        if (user.getEmail().equals(email)) {
            return user;
        }

        user.setEmail(email);
        user.setVerificationId(UUID.randomUUID());

        if (user.getUniversity() != null && !user.getUniversity().ownsEmail(email)) {
            user.setUniversity(null);
            user.setDegree(null);
        }

        eventPublisher.publishEvent(new SendVerificationEmailEvent(user));
        return userRepository.save(user);
    }

    public User updateUserPassword(User user, UpdateUserPasswordDto dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }

    public User updateUserRole(User user, Role role) {
        if (user.getRole() == Role.ADMIN && !userRepository.existsByRoleAndIdNot(Role.ADMIN, user.getId())) {
            throw new OnlyAdminException();
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        if (user.getRole() == Role.ADMIN && !userRepository.existsByRoleAndIdNot(Role.ADMIN, user.getId())) {
            throw new OnlyAdminException();
        }

        userRepository.delete(user);
    }

    public void checkUserVerified(User user) {
        if (!user.getVerified()) {
            throw new UserNotVerifiedException();
        }
    }

    public int detachUniversity(UUID universityId) {
        return userRepository.detachUniversity(universityId);
    }

    public User updateUserProfilePicture(User user, MultipartFile file) throws IOException {
        if (!fileInfoService.isContentTypeImage(file.getContentType())) {
            throw new UnsupportedMediaTypeException();
        }

        FileInfo oldPicture = user.getProfilePicture();
        FileInfo newPicture = fileInfoService.createFile(file);

        user.setProfilePicture(newPicture);
        user = userRepository.save(user);

        if (oldPicture != null) {
            fileInfoService.deleteFile(oldPicture);
        }
        return user;
    }

    public User deleteUserProfilePicture(User user) {
        FileInfo oldPicture = user.getProfilePicture();

        if (oldPicture == null) {
            throw new ConflictException("User does not have a profile picture");
        }

        user.setProfilePicture(null);
        user = userRepository.save(user);
        fileInfoService.deleteFile(oldPicture);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

}

package com.turinmachin.unilife.authentication.domain;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.exception.AuthProviderNotCredentialsException;
import com.turinmachin.unilife.authentication.exception.AuthProviderNotGoogleException;
import com.turinmachin.unilife.authentication.exception.InvalidCredentialsException;
import com.turinmachin.unilife.authentication.exception.InvalidVerificationIdException;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.SelfUserResponseDto;
import com.turinmachin.unilife.user.event.SendWelcomeEmailEvent;
import com.turinmachin.unilife.user.exception.UserAlreadyVerifiedException;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final GoogleOAuthService googleAuthenticationService;
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public LoginResponseDto jwtLogin(final JwtAuthLoginDto dto) {
        final User user = userService.getUserByUsernameOrEmail(dto.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException();

        final String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, SelfUserResponseDto.class));
    }

    public LoginResponseDto jwtRegister(final RegisterUserDto dto) {
        final User createdUser = userService.createUser(dto);
        userService.sendVerificationEmail(createdUser);

        final String token = jwtService.generateToken(createdUser);
        return new LoginResponseDto(token, modelMapper.map(createdUser, SelfUserResponseDto.class));
    }

    public LoginResponseDto googleAuth(final String idTokenValue) throws IOException, GeneralSecurityException {
        final GoogleIdToken idToken = googleAuthenticationService.verifyIdToken(idTokenValue);
        final Payload payload = idToken.getPayload();

        final String email = payload.get("email").toString();

        final User user = userService.getUserByEmail(email).orElseGet(() -> {
            final User createdUser = userService.createGoogleUser(email, payload);
            eventPublisher.publishEvent(new SendWelcomeEmailEvent(createdUser));
            return createdUser;
        });

        if (user.getAuthProvider() != AuthProvider.GOOGLE) {
            throw new AuthProviderNotGoogleException();
        }

        final String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, SelfUserResponseDto.class));
    }

    public LoginResponseDto googleAuthUpgrade(final String idTokenValue) throws IOException, GeneralSecurityException {
        final GoogleIdToken idToken = googleAuthenticationService.verifyIdToken(idTokenValue);
        final Payload payload = idToken.getPayload();

        final String email = payload.get("email").toString();
        final User user = userService.getUserByEmail(email).orElseThrow(UserNotFoundException::new);

        if (user.getAuthProvider() != AuthProvider.CREDENTIALS) {
            throw new AuthProviderNotCredentialsException();
        }

        userService.upgradeUserAuthToGoogle(user, payload);

        final String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, SelfUserResponseDto.class));
    }

    public User tryVerifyUser(final User user, final UUID verificationId) {
        if (user.getVerified())
            throw new UserAlreadyVerifiedException();

        if (!user.getVerificationId().equals(verificationId))
            throw new InvalidVerificationIdException();

        eventPublisher.publishEvent(new SendWelcomeEmailEvent(user));
        return userService.verifyUser(user);
    }

    public void triggerResetPassword(final User user) {
        if (user.getPassword() == null)
            throw new ConflictException("User does not have a password");

        if (userService.userHasValidToken(user))
            throw new ConflictException("User already has reset token");

        final String tokenValue = generateSecureToken(32);
        userService.setResetPasswordToken(user, tokenValue);
    }

    public boolean verifyPasswordToken(final String token) {
        return userService.userTokenExistsByValue(token);
    }

    public User resetUserPassword(final String token, final String newPassword) {
        final User user = userService.getUserByPasswordResetToken(token).orElseThrow(UserNotFoundException::new);

        return userService.resetPassword(user, newPassword);
    }

    private String generateSecureToken(final int lengthBytes) {
        final byte[] randomBytes = new byte[lengthBytes];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}

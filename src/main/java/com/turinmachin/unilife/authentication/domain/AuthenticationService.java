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
import com.turinmachin.unilife.authentication.dto.GoogleLoginRequestDto;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.dto.RegisterResponseDto;
import com.turinmachin.unilife.authentication.exception.AuthProviderNotCredentialsException;
import com.turinmachin.unilife.authentication.exception.AuthProviderNotGoogleException;
import com.turinmachin.unilife.authentication.exception.InvalidCredentialsException;
import com.turinmachin.unilife.authentication.exception.InvalidVerificationIdException;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
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

    public LoginResponseDto jwtLogin(JwtAuthLoginDto dto) {
        User user = userService.getUserByUsernameOrEmail(dto.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getAuthProvider() != AuthProvider.CREDENTIALS)
            throw new AuthProviderNotCredentialsException();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException();

        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, UserResponseDto.class));
    }

    public LoginResponseDto jwtRegister(RegisterUserDto dto) {
        User createdUser = userService.createUser(dto);
        userService.sendVerificationEmail(createdUser);

        String token = jwtService.generateToken(createdUser);
        return new LoginResponseDto(token, modelMapper.map(createdUser, UserResponseDto.class));
    }

    public LoginResponseDto googleAuth(String idTokenValue) throws IOException, GeneralSecurityException {
        GoogleIdToken idToken = googleAuthenticationService.verifyIdToken(idTokenValue);
        Payload payload = idToken.getPayload();

        String email = payload.get("email").toString();

        User user = userService.getUserByEmail(email).orElseGet(() -> {
            User createdUser = userService.createGoogleUser(email, payload);
            eventPublisher.publishEvent(new SendWelcomeEmailEvent(createdUser));
            return createdUser;
        });

        if (user.getAuthProvider() != AuthProvider.GOOGLE) {
            throw new AuthProviderNotGoogleException();
        }

        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, UserResponseDto.class));
    }

    public User tryVerifyUser(User user, UUID verificationId) {
        if (user.getAuthProvider() != AuthProvider.CREDENTIALS) {
            throw new AuthProviderNotCredentialsException();
        }

        if (user.getVerified())
            throw new UserAlreadyVerifiedException();

        if (!user.getVerificationId().equals(verificationId))
            throw new InvalidVerificationIdException();

        eventPublisher.publishEvent(new SendWelcomeEmailEvent(user));
        return userService.verifyUser(user);
    }

    public void triggerResetPassword(User user) {
        if (user.getAuthProvider() != AuthProvider.CREDENTIALS)
            throw new AuthProviderNotCredentialsException();

        if (userService.userHasValidToken(user))
            throw new ConflictException("User already has reset token");

        String tokenValue = generateSecureToken(32);
        userService.setResetPasswordToken(user, tokenValue);
    }

    public boolean verifyPasswordToken(String token) {
        return userService.userTokenExistsByValue(token);
    }

    public User resetUserPassword(String token, String newPassword) {
        User user = userService.getUserByPasswordResetToken(token).orElseThrow(UserNotFoundException::new);

        if (user.getAuthProvider() != AuthProvider.CREDENTIALS)
            throw new AuthProviderNotCredentialsException();

        return userService.resetPassword(user, newPassword);
    }

    private String generateSecureToken(int lengthBytes) {
        byte[] randomBytes = new byte[lengthBytes];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}

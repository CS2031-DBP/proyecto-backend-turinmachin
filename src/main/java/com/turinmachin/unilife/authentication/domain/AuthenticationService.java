package com.turinmachin.unilife.authentication.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.exception.InvalidCredentialsException;
import com.turinmachin.unilife.authentication.exception.InvalidVerificationIdException;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.common.utils.HashUtils;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.exception.UserAlreadyVerifiedException;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public LoginResponseDto jwtLogin(JwtAuthLoginDto dto) {
        User user = userService.getUserByUsernameOrEmail(dto.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);
        return new LoginResponseDto(token, modelMapper.map(user, UserResponseDto.class));
    }

    public User tryVerifyUser(User user, UUID verificationId) {
        if (user.getVerified()) {
            throw new UserAlreadyVerifiedException();
        }

        if (!user.getVerificationId().equals(verificationId)) {
            throw new InvalidVerificationIdException();
        }

        return userService.verifyUser(user);
    }

    public void triggerResetPassword(User user) {
        if (user.getPasswordResetToken() != null) {
            throw new ConflictException("User already has reset token");
        }

        String tokenValue = generateSecureToken(32);
        userService.setResetPasswordToken(user, tokenValue);
    }

    public boolean verifyPasswordToken(String token) {
        return userService.userTokenExistsByValue(token);
    }

    public User resetUserPassword(String token, String newPassword) {
        User user = userService.getUserByPasswordResetToken(token).orElseThrow(UserNotFoundException::new);
        return userService.resetPassword(user, newPassword);
    }

    private String generateSecureToken(int lengthBytes) {
        byte[] randomBytes = new byte[lengthBytes];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}

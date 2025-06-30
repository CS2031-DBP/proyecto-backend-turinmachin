package com.turinmachin.unilife.authentication.domain;

import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.exception.InvalidCredentialsException;
import com.turinmachin.unilife.authentication.exception.InvalidVerificationIdException;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.exception.UserAlreadyVerifiedException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

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

}

package com.turinmachin.unilife.authentication.domain;

import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.JwtAuthResponseDto;
import com.turinmachin.unilife.authentication.exception.InvalidCredentialsException;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JwtAuthResponseDto jwtLogin(JwtAuthLoginDto dto) {
        User user = userService.getUserByUsernameOrEmail(dto.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        JwtAuthResponseDto response = new JwtAuthResponseDto();
        response.setToken(jwtService.generateToken(user));
        return response;
    }
}

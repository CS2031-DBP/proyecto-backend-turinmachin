package com.turinmachin.unilife.authentication.domain;

import com.turinmachin.unilife.jwt.domain.JwtService;
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

}

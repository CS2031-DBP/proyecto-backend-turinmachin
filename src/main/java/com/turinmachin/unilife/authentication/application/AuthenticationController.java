package com.turinmachin.unilife.authentication.application;

import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.JwtAuthResponseDto;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.event.SendVerificationEmailEvent;
import com.turinmachin.unilife.user.event.SendWelcomeEmailEvent;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public JwtAuthResponseDto login(@Valid @RequestBody JwtAuthLoginDto dto) {
        return authenticationService.jwtLogin(dto);
    }

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody RegisterUserDto dto) {
        User createdUser = userService.createUser(dto);
        eventPublisher.publishEvent(new SendVerificationEmailEvent(createdUser));
        return modelMapper.map(createdUser, UserResponseDto.class);
    }

    @PostMapping("/verify")
    public UserResponseDto verifyUser(@Valid @RequestBody VerifyUserDto dto) {
        User user = authenticationService.verifyUser(dto.getVerificationId());
        eventPublisher.publishEvent(new SendWelcomeEmailEvent(user));
        return modelMapper.map(user, UserResponseDto.class);
    }

}

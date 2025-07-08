package com.turinmachin.unilife.authentication.application;

import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.dto.ResetPasswordDto;
import com.turinmachin.unilife.authentication.dto.IssuePasswordResetDto;
import com.turinmachin.unilife.authentication.dto.TokenRequestDto;
import com.turinmachin.unilife.authentication.dto.TokenVerifyResponseDto;
import com.turinmachin.unilife.authentication.dto.VerifyUserDto;
import com.turinmachin.unilife.jwt.domain.JwtService;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.event.SendWelcomeEmailEvent;
import com.turinmachin.unilife.user.exception.UserAlreadyVerifiedException;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody JwtAuthLoginDto dto) {
        return authenticationService.jwtLogin(dto);
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody RegisterUserDto dto) {
        User createdUser = userService.createUser(dto);
        userService.sendVerificationEmail(createdUser);

        String token = jwtService.generateToken(createdUser);
        return new LoginResponseDto(token, modelMapper.map(createdUser, UserResponseDto.class));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponseDto verifyUser(@Valid @RequestBody VerifyUserDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        authenticationService.tryVerifyUser(user, dto.getVerificationId());

        eventPublisher.publishEvent(new SendWelcomeEmailEvent(user));
        return modelMapper.map(user, UserResponseDto.class);
    }

    @PostMapping("/verify-resend")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerificationEmail(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getVerified()) {
            throw new UserAlreadyVerifiedException();
        }

        userService.sendVerificationEmail(user);
    }

    @PostMapping("/request-password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issuePasswordReset(@Valid @RequestBody IssuePasswordResetDto dto) {
        User user = userService.getUserByEmail(dto.getEmail()).orElseThrow(UserNotFoundException::new);
        authenticationService.triggerResetPassword(user);
    }

    @PostMapping("/verify-reset-token")
    public TokenVerifyResponseDto verifyPasswordToken(@Valid @RequestBody TokenRequestDto dto) {
        boolean valid = authenticationService.verifyPasswordToken(dto.getToken());
        return new TokenVerifyResponseDto(valid);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        authenticationService.resetUserPassword(dto.getToken(), dto.getNewPassword());
    }

}

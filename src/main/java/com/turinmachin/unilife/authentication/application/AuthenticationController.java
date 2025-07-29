package com.turinmachin.unilife.authentication.application;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.authentication.dto.IssuePasswordResetDto;
import com.turinmachin.unilife.authentication.dto.JwtAuthLoginDto;
import com.turinmachin.unilife.authentication.dto.LoginResponseDto;
import com.turinmachin.unilife.authentication.dto.GoogleLoginRequestDto;
import com.turinmachin.unilife.authentication.dto.ResetPasswordDto;
import com.turinmachin.unilife.authentication.dto.TokenRequestDto;
import com.turinmachin.unilife.authentication.dto.TokenVerifyResponseDto;
import com.turinmachin.unilife.authentication.dto.VerifyUserDto;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.RegisterUserDto;
import com.turinmachin.unilife.user.dto.SelfUserResponseDto;
import com.turinmachin.unilife.user.exception.UserAlreadyVerifiedException;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody final JwtAuthLoginDto dto) {
        return authenticationService.jwtLogin(dto);
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody final RegisterUserDto dto) {
        return authenticationService.jwtRegister(dto);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SelfUserResponseDto verifyUser(@Valid @RequestBody final VerifyUserDto dto,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        authenticationService.tryVerifyUser(user, dto.getVerificationId());

        return modelMapper.map(user, SelfUserResponseDto.class);
    }

    @PostMapping("/verify-resend")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerificationEmail(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        if (user.getVerified()) {
            throw new UserAlreadyVerifiedException();
        }

        userService.sendVerificationEmail(user);
    }

    @PostMapping("/request-password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issuePasswordReset(@Valid @RequestBody final IssuePasswordResetDto dto) {
        final User user = userService.getUserByEmail(dto.getEmail()).orElseThrow(UserNotFoundException::new);
        authenticationService.triggerResetPassword(user);
    }

    @PostMapping("/verify-reset-token")
    public TokenVerifyResponseDto verifyPasswordToken(@Valid @RequestBody final TokenRequestDto dto) {
        final boolean valid = authenticationService.verifyPasswordToken(dto.getToken());
        return new TokenVerifyResponseDto(valid);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody final ResetPasswordDto dto) {
        authenticationService.resetUserPassword(dto.getToken(), dto.getNewPassword());
    }

    @PostMapping("/oauth/google/login")
    public LoginResponseDto googleLogin(@Valid @RequestBody final GoogleLoginRequestDto dto)
            throws IOException, GeneralSecurityException {
        return authenticationService.googleLogin(dto.getIdToken());
    }

    @PostMapping("/oauth/google/register")
    public LoginResponseDto googleRegister(@Valid @RequestBody final GoogleLoginRequestDto dto)
            throws IOException, GeneralSecurityException {
        return authenticationService.googleRegister(dto.getIdToken());
    }

    @PostMapping("/oauth/google/upgrade")
    public LoginResponseDto googleUpgradeLogin(@Valid @RequestBody final GoogleLoginRequestDto dto)
            throws IOException, GeneralSecurityException {
        return authenticationService.googleAuthUpgrade(dto.getIdToken());
    }

}

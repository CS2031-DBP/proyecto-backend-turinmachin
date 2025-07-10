package com.turinmachin.unilife.authentication.application;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.turinmachin.unilife.authentication.domain.AuthProvider;
import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.authentication.domain.GoogleOAuthService;
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
import com.turinmachin.unilife.user.dto.UserResponseDto;
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
    public LoginResponseDto login(@Valid @RequestBody JwtAuthLoginDto dto) {
        return authenticationService.jwtLogin(dto);
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody RegisterUserDto dto) {
        return authenticationService.jwtRegister(dto);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponseDto verifyUser(@Valid @RequestBody VerifyUserDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        authenticationService.tryVerifyUser(user, dto.getVerificationId());

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

    @PostMapping("/oauth/google")
    public LoginResponseDto googleAuth(@Valid @RequestBody GoogleLoginRequestDto dto)
            throws IOException, GeneralSecurityException {
        return authenticationService.googleAuth(dto.getIdToken());
    }

}

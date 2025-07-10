package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.UnauthorizedException;

public class AuthProviderNotCredentialsException extends UnauthorizedException {

    public AuthProviderNotCredentialsException() {
        super("User does not use credentials authorization");
    }

}

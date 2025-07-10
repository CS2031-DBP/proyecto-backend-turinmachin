package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.UnauthorizedException;

public class AuthProviderNotGoogleException extends UnauthorizedException {

    public AuthProviderNotGoogleException() {
        super("User does not use google authorization");
    }

}

package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.ForbiddenException;

public class AuthProviderNotCredentialsException extends ForbiddenException {

    public AuthProviderNotCredentialsException() {
        super("User does not use credentials authorization");
    }

}

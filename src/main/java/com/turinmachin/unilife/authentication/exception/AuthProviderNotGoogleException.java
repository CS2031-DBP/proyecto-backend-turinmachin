package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.ForbiddenException;

public class AuthProviderNotGoogleException extends ForbiddenException {

    public AuthProviderNotGoogleException() {
        super("User does not use google authorization");
    }

}

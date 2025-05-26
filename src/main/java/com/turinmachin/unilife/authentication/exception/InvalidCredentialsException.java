package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Invalid username/email or password");
    }

}

package com.turinmachin.unilife.authentication.exception;

import com.turinmachin.unilife.common.exception.UnauthorizedException;

public class InvalidVerificationIdException extends UnauthorizedException {

    public InvalidVerificationIdException() {
        super("Invalid verification ID");
    }

}

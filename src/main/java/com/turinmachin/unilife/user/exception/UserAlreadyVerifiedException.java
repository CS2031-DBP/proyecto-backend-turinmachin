package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UserAlreadyVerifiedException extends ConflictException {
    public UserAlreadyVerifiedException() {
        super("User is already verified");
    }
}

package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UsernameConflictException extends ConflictException {

    public UsernameConflictException() {
        super("User with username already exists");
    }

}

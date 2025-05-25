package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class EmailConflictException extends ConflictException {

    public EmailConflictException() {
        super("User with email already exists");
    }

}

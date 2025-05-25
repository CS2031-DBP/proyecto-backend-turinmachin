package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class OnlyAdminException extends ConflictException {

    public OnlyAdminException() {
        super("User is the only current admin");
    }

}

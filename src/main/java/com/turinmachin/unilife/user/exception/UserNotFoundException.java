package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super("User not found");
    }

}

package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ForbiddenException;

public class UserNotVerifiedException extends ForbiddenException {

    public UserNotVerifiedException() {
        super("User is not verified");
    }

}

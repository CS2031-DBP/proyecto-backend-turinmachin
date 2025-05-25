package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.ForbiddenException;

public class UserWithoutUniversityException extends ForbiddenException {

    public UserWithoutUniversityException() {
        super("User is not associated to a university");
    }

}

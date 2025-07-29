package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UniversityNotOwnEmailException extends ConflictException {

    public UniversityNotOwnEmailException() {
        super("Email does not belong to university");
    }

}

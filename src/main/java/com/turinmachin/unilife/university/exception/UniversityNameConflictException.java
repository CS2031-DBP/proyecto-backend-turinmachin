package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UniversityNameConflictException extends ConflictException {

    public UniversityNameConflictException() {
        super("University name is taken");
    }

}

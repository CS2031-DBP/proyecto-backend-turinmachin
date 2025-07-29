package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UniversityShortNameConflictException extends ConflictException {

    public UniversityShortNameConflictException() {
        super("University short name is taken");
    }

}

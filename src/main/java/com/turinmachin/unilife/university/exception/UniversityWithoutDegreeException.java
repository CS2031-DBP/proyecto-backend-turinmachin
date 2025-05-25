package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class UniversityWithoutDegreeException extends ConflictException {

    public UniversityWithoutDegreeException() {
        super("University does not have degree");
    }

}

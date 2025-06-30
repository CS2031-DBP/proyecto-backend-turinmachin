package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class DegreeAlreadyPresentException extends ConflictException {

    public DegreeAlreadyPresentException() {
        super("University already has degree");
    }

}

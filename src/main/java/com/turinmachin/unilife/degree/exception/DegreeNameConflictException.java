package com.turinmachin.unilife.degree.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class DegreeNameConflictException extends ConflictException {

    public DegreeNameConflictException() {
        super("Degree name is taken");
    }

}

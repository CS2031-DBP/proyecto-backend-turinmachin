package com.turinmachin.unilife.degree.exception;

import com.turinmachin.unilife.common.exception.ConflictException;

public class DegreeShortNameConflictException extends ConflictException {

    public DegreeShortNameConflictException() {
        super("Degree short name is taken");
    }

}

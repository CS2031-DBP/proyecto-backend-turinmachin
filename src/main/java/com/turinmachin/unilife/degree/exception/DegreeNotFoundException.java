package com.turinmachin.unilife.degree.exception;

import com.turinmachin.unilife.common.exception.NotFoundException;

public class DegreeNotFoundException extends NotFoundException {

    public DegreeNotFoundException() {
        super("Degree not found");
    }

}

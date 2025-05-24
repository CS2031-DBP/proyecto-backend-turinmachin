package com.turinmachin.unilife.university.exception;

import com.turinmachin.unilife.common.exception.NotFoundException;

public class UniversityNotFoundException extends NotFoundException {

    public UniversityNotFoundException() {
        super("University not found");
    }

}

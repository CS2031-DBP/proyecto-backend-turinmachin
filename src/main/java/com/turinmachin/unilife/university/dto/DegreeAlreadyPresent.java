package com.turinmachin.unilife.university.dto;

import com.turinmachin.unilife.common.exception.ConflictException;

public class DegreeAlreadyPresent extends ConflictException {

    public DegreeAlreadyPresent() {
        super("University already has degree");
    }

}

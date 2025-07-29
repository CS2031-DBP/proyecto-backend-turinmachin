package com.turinmachin.unilife.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConflictException extends ResponseStatusException {

    public ConflictException() {
        this("Conflict");
    }

    public ConflictException(final String message) {
        super(HttpStatus.CONFLICT, message);
    }

}

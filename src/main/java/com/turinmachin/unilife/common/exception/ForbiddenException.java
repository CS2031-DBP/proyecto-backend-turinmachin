package com.turinmachin.unilife.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ForbiddenException extends ResponseStatusException {

    public ForbiddenException() {
        this("Forbidden");
    }

    public ForbiddenException(final String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}

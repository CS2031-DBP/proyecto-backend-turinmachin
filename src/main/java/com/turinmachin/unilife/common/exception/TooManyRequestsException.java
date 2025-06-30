package com.turinmachin.unilife.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TooManyRequestsException extends ResponseStatusException {

    public TooManyRequestsException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
    }

    public TooManyRequestsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }

}

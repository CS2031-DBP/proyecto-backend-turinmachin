package com.turinmachin.unilife.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnsupportedMediaTypeException extends ResponseStatusException {

    public UnsupportedMediaTypeException() {
        this("Unsupported media type");
    }

    public UnsupportedMediaTypeException(final String message) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }
}

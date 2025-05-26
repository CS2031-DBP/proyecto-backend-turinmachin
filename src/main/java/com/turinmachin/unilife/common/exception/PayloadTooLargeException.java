package com.turinmachin.unilife.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PayloadTooLargeException extends ResponseStatusException {

    public PayloadTooLargeException() {
        this("Payload too large");
    }

    public PayloadTooLargeException(String message) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, message);
    }

}

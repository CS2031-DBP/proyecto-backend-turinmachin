package com.turinmachin.unilife.image.exception;

import org.apache.coyote.BadRequestException;

public class EmptyImageException extends BadRequestException {

    public EmptyImageException() {
        super("Empty image file");
    }

}

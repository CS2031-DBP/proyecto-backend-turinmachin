package com.turinmachin.unilife.post.exception;

import com.turinmachin.unilife.common.exception.InternalServerErrorException;

public class TagGenerationException extends InternalServerErrorException {
    public TagGenerationException() {
        super("Ocurrió un error generando los tags");
    }
}

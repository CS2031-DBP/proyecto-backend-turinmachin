package com.turinmachin.unilife.ai.exception;

import com.turinmachin.unilife.common.exception.InternalServerErrorException;

public class AIResponseException extends InternalServerErrorException {
    public AIResponseException() {
        super("No se recibió una respuesta válida del modelo de AI.");
    }
}
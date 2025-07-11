package com.turinmachin.unilife.ai.exception;

public class AIResponseException extends RuntimeException {
    public AIResponseException() {
        super("No se recibió una respuesta válida del modelo de AI.");
    }
}
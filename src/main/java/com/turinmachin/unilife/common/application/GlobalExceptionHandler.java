package com.turinmachin.unilife.common.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.turinmachin.unilife.common.dto.ResponseStatusDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseStatusDto> handleResponseStatusException(ResponseStatusException e) {
        ResponseStatusDto response = new ResponseStatusDto();
        response.setStatus(e.getStatusCode().value());
        response.setMessage(e.getReason());

        return new ResponseEntity<>(response, e.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStatusDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError firstError = e.getFieldErrors().getFirst();

        ResponseStatusDto response = new ResponseStatusDto();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(firstError.getField() + ": " + firstError.getDefaultMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

package com.turinmachin.unilife.fileinfo.exception;

import org.apache.coyote.BadRequestException;

public class EmptyFileException extends BadRequestException {

    public EmptyFileException() {
        super("File is empty");
    }

}

package com.turinmachin.unilife.fileinfo.exception;

import com.turinmachin.unilife.common.exception.PayloadTooLargeException;

public class FileTooLargeException extends PayloadTooLargeException {

    public FileTooLargeException() {
        super("File is too large");
    }

}

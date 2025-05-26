package com.turinmachin.unilife.image.exception;

import com.turinmachin.unilife.common.exception.PayloadTooLargeException;

public class ImageTooLargeException extends PayloadTooLargeException {

    public ImageTooLargeException() {
        super("Image too large");
    }

}

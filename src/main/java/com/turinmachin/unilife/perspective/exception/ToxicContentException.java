package com.turinmachin.unilife.perspective.exception;

import com.turinmachin.unilife.common.exception.ForbiddenException;

public class ToxicContentException extends ForbiddenException {
    public ToxicContentException() {
        super("Content flagged as toxic");
    }
}

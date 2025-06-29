package com.turinmachin.unilife.user.exception;

import com.turinmachin.unilife.common.exception.TooManyRequestsException;

public class VerificationEmailCooldownException extends TooManyRequestsException {

    public VerificationEmailCooldownException() {
        super("Verification email cooldown not finished");
    }

}

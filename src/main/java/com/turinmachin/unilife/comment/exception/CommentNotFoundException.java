package com.turinmachin.unilife.comment.exception;

import com.turinmachin.unilife.common.exception.NotFoundException;

public class CommentNotFoundException extends NotFoundException {

    public CommentNotFoundException() {
        super("Comment not found");
    }

}

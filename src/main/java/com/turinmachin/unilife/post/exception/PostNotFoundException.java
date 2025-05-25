package com.turinmachin.unilife.post.exception;

import com.turinmachin.unilife.common.exception.NotFoundException;

public class PostNotFoundException extends NotFoundException {

    public PostNotFoundException() {
        super("Post not found");
    }

}

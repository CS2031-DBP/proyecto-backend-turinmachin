package com.turinmachin.unilife.comment.dto;

import com.turinmachin.unilife.user.dto.UserResponseDto;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentResponseDto {

    private UUID id;
    private UserResponseDto author;
    private String content;
}

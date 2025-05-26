package com.turinmachin.unilife.comment.dto;

import java.util.List;
import java.util.UUID;

import com.turinmachin.unilife.user.dto.UserResponseDto;

import lombok.Data;

@Data
public class CommentResponseDto {

    private UUID id;
    private UserResponseDto author;
    private String content;
    private List<CommentResponseDto> replies;

}

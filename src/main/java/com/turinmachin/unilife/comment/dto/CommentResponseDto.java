package com.turinmachin.unilife.comment.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.user.dto.UserResponseDto;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponseDto {

    private UUID id;
    private UserResponseDto author;
    private String content;
    private List<CommentResponseDto> replies;
    private Instant createdAt;
    private Instant updatedAt;

}

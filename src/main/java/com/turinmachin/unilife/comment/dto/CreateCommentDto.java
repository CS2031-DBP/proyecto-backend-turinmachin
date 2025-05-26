package com.turinmachin.unilife.comment.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDto {

    private UUID parentId;

    @NotBlank
    private String content;

}

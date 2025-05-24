package com.turinmachin.unilife.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentDto {

    @NotBlank
    private String content;
}

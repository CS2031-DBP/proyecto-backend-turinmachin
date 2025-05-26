package com.turinmachin.unilife.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDto {

    @NotBlank
    private String content;

}

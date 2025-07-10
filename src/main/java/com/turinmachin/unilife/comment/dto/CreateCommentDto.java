package com.turinmachin.unilife.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentDto {

    @NotBlank
    @Size(max = 400)
    private String content;

}

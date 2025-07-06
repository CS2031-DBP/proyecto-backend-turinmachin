package com.turinmachin.unilife.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPromptRequestDto {

    @NotBlank
    private String content;
}

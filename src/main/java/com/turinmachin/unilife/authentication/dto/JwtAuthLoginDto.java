package com.turinmachin.unilife.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtAuthLoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

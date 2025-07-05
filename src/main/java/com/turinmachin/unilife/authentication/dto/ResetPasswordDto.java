package com.turinmachin.unilife.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 4)
    private String newPassword;

}

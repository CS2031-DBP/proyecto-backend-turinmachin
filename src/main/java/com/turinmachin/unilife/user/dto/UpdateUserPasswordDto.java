package com.turinmachin.unilife.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordDto {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 4)
    private String newPassword;

}

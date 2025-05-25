package com.turinmachin.unilife.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserEmailDto {

    @NotBlank
    @Email
    private String email;

}

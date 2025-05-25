package com.turinmachin.unilife.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3)
    private String username;

    @Size(min = 3)
    private String displayName;

    @NotBlank
    @Size(min = 4)
    private String password;

}

package com.turinmachin.unilife.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {

    @NotBlank
    @Email
    private String email;

    @Size(min = 3, max = 16)
    @Pattern(regexp = "^[a-zA-Z0-9.\\-_]*$")
    private String username;

    @Size(min = 3, max = 36)
    private String displayName;

    @NotBlank
    @Size(min = 4)
    private String password;

}

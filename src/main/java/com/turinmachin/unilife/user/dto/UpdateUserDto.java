package com.turinmachin.unilife.user.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 16)
    @Pattern(regexp = "^[a-zA-Z0-9.\\-_]*$")
    private String username;

    @Size(min = 3, max = 36)
    private String displayName;

    private String bio;

    private LocalDate birthday;

    private UUID degreeId;

}

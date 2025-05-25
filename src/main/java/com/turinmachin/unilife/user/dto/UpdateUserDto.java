package com.turinmachin.unilife.user.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {

    @NotBlank
    @Size(min = 3)
    private String username;

    @Size(min = 3)
    private String displayName;

    private String bio;

    private UUID universityId;

    private UUID degreeId;

}

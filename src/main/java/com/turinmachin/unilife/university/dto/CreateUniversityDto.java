package com.turinmachin.unilife.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUniversityDto {

    @NotBlank
    private String name;

    @NotNull
    @Size(min = 1)
    private List<String> emailDomains;

    @NotNull
    private List<UUID> degreeIds;
}

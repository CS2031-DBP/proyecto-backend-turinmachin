package com.turinmachin.unilife.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateUniversityDto {

    @NotBlank
    private String name;

    private String shortName;

    private String websiteUrl;

    @NotNull
    @Size(min = 1)
    private Set<String> emailDomains;

    @NotNull
    private Set<UUID> degreeIds;
}

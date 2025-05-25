package com.turinmachin.unilife.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUniversityDto {

    @NotBlank
    public String name;

    @NotNull
    @Size(min = 1)
    private List<String> emailDomains;

}

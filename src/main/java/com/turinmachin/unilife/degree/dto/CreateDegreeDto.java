package com.turinmachin.unilife.degree.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDegreeDto {

    @NotBlank
    private String name;

    private String shortName;

}

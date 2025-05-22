package com.turinmachin.unilife.degree.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDegreeDto {

    @NotBlank
    private String name;

}

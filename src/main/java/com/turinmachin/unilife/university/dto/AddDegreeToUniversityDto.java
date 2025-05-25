package com.turinmachin.unilife.university.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddDegreeToUniversityDto {

    @NotNull
    private UUID degreeId;

}

package com.turinmachin.unilife.degree.dto;

import com.turinmachin.unilife.university.dto.UniversityResponseDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DegreeResponseDto {

    private UUID id;
    private String name;
    private List<UniversityResponseDto> universities;

}

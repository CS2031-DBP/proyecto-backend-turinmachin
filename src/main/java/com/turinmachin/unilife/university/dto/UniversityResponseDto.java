package com.turinmachin.unilife.university.dto;

import com.turinmachin.unilife.degree.dto.DegreeResponseDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UniversityResponseDto {

    private UUID id;
    private String name;
    private String websiteUrl;
    private List<String> emailDomains;
    private List<DegreeResponseDto> degrees;

}

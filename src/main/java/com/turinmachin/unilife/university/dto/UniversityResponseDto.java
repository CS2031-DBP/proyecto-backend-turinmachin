package com.turinmachin.unilife.university.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniversityResponseDto {

    private UUID id;
    private String name;
    private String shortName;
    private String websiteUrl;
    private List<String> emailDomains;

}

package com.turinmachin.unilife.university.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniversityResponseDto {

    private UUID id;
    private String name;
    private String shortName;
    private String websiteUrl;
    private List<String> emailDomains;
    private FileInfoResponseDto picture;

}

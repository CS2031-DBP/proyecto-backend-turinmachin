package com.turinmachin.unilife.university.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.fileinfo.dto.FileInfoResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniversityWithStatsDto {

    private UUID id;
    private String name;
    private String shortName;
    private String websiteUrl;
    private FileInfoResponseDto picture;
    private Long totalStudents;

}

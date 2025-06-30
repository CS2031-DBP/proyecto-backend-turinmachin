package com.turinmachin.unilife.degree.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DegreeWithStatsDto {

    private UUID id;
    private String name;
    private String shortName;
    private Long totalUniversities;
    private Long totalStudents;

}

package com.turinmachin.unilife.degree.dto;

import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DegreeResponseDto {

    private UUID id;
    private String name;

}

package com.turinmachin.unilife.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthResponseDto {

    private String token;

}

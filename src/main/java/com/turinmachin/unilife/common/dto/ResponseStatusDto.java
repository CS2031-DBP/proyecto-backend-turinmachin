package com.turinmachin.unilife.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStatusDto {

    private Integer status;

    private String message;

}

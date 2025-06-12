package com.turinmachin.unilife.fileinfo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileInfoResponseDto {

    private String url;
    private String mediaType;

}

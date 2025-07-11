package com.turinmachin.unilife.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turinmachin.unilife.user.dto.SelfUserResponseDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDto {

    private String token;
    private SelfUserResponseDto user;

}

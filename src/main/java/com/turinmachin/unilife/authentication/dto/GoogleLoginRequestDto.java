package com.turinmachin.unilife.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequestDto {

    @NotBlank
    private String idToken;

}

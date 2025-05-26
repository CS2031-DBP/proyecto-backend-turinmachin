package com.turinmachin.unilife.authentication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class VerifyUserDto {

    @NotNull
    private UUID verificationId;

}

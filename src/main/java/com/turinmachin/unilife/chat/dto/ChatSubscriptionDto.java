package com.turinmachin.unilife.chat.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatSubscriptionDto {

    @URL
    @NotNull
    private String endpoint;

    @NotBlank
    private String auth;

    @NotBlank
    private String key;

}

package com.turinmachin.unilife.chat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WebhookRequestDto {

    @Valid
    @NotNull
    private ChatMessageDto record;

}

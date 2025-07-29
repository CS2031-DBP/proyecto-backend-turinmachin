package com.turinmachin.unilife.chat.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageDto {

    @NotNull
    private UUID id;

    @NotNull
    @JsonProperty("from_id")
    private UUID fromId;

    @NotNull
    @JsonProperty("to_id")
    private UUID toId;

    @NotNull
    private String content;

    @NotNull
    private boolean read;

    @NotNull
    @JsonProperty("created_at")
    private Instant createdAt;

}

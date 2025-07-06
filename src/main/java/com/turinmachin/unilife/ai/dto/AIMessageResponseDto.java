package com.turinmachin.unilife.ai.dto;

import com.turinmachin.unilife.ai.domain.AuthorRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIMessageResponseDto {

    private UUID id;
    private AuthorRole role;
    private String content;
    private Instant createdAt;
}

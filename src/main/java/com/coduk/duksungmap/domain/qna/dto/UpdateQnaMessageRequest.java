package com.coduk.duksungmap.domain.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateQnaMessageRequest(
        @NotBlank
        @Size(max = 1000)
        String content
) {}
package com.coduk.duksungmap.domain.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateQnaThreadRequest(
        @NotBlank
        @Size(max = 500)
        String content
) {}
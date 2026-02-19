package com.coduk.duksungmap.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(
        @NotBlank String duksungId,
        @NotBlank String code
) {}
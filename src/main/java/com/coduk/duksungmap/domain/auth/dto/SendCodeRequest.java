package com.coduk.duksungmap.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SendCodeRequest(
        @NotBlank String duksungId
) {}
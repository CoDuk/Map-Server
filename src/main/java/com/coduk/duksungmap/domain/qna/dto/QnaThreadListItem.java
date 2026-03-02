package com.coduk.duksungmap.domain.qna.dto;

import java.time.LocalDateTime;

public record QnaThreadListItem(
        Long threadId,
        Long userId,
        String content,
        boolean answered,
        LocalDateTime createdAt
) {}
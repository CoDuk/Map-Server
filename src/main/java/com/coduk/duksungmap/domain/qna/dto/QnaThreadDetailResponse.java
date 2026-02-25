package com.coduk.duksungmap.domain.qna.dto;

import java.time.LocalDateTime;

public record QnaThreadDetailResponse(
        Long threadId,
        Long userId,
        String content,
        LocalDateTime createdAt,
        boolean answered,
        AnswerResponse answer // 답변 (없으면 null)

) {
    public record AnswerResponse(
            Long messageId,
            Long adminId,
            String content,
            LocalDateTime createdAt
    ) {}
}
package com.coduk.duksungmap.domain.qna.dto;

import java.util.List;

public record QnaThreadListResponse(
        List<QnaThreadListItem> threads
) {}
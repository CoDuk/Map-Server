package com.coduk.duksungmap.domain.qna.exception;

import com.coduk.duksungmap.global.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QnaErrorCode implements BaseCode {

    // 질문
    THREAD_NOT_FOUND(HttpStatus.NOT_FOUND, "QNA-001", "존재하지 않는 질문입니다."),
    THREAD_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "QNA-002", "이미 삭제된 질문입니다."),

    // 답변
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "QNA-101", "존재하지 않는 답변입니다."),
    ANSWER_ALREADY_EXISTS(HttpStatus.CONFLICT, "QNA-102", "이미 답변이 등록된 질문입니다."),
    ANSWER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "QNA-103", "이미 삭제된 답변입니다."),

    ADMIN_ONLY(HttpStatus.FORBIDDEN, "QNA-201", "관리자만 수행할 수 있는 기능입니다."),
    CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "QNA-202", "내용은 비어 있을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
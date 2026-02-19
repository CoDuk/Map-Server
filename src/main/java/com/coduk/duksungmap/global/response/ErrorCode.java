package com.coduk.duksungmap.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-001", "서버 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-002", "요청 파라미터가 올바르지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "COMMON-003", "JSON 파싱에 실패했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "COMMON-004", "데이터 무결성 제약조건 위반입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
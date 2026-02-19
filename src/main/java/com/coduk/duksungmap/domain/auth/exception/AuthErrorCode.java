package com.coduk.duksungmap.domain.auth.exception;

import com.coduk.duksungmap.global.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "AUTH-001", "아이디 형식이 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH-002", "이미 가입된 이메일입니다."),
    EMAIL_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH-003", "인증 코드가 만료되었습니다."),
    EMAIL_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "AUTH-004", "인증 코드가 일치하지 않습니다."),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-005", "이메일 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
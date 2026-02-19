package com.coduk.duksungmap.domain.user.exception;

import com.coduk.duksungmap.global.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "존재하지 않는 사용자입니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "USER-002", "탈퇴한 사용자입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
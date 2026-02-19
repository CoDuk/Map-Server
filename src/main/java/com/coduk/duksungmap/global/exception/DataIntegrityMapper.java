package com.coduk.duksungmap.global.exception;

import com.coduk.duksungmap.global.response.BaseCode;

/**
 * 데이터 무결성 제약 조건이 터졌을때 이 매퍼 클래스를 상속 받으세요.
 */
public interface DataIntegrityMapper {
    boolean supports(String key);
    BaseCode errorCode();
}
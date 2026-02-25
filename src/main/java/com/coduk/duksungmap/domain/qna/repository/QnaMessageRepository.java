package com.coduk.duksungmap.domain.qna.repository;

import com.coduk.duksungmap.domain.qna.entity.QnaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnaMessageRepository extends JpaRepository<QnaMessage, Long> {

    // 특정 질문의 답변 1개 조회
    Optional<QnaMessage> findFirstByThreadIdOrderByCreatedAtAsc(Long threadId);

    // 해당 질문에 답변이 존재하는지 확인
    boolean existsByThreadId(Long threadId);
}
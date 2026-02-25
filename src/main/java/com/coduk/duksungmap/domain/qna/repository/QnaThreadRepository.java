package com.coduk.duksungmap.domain.qna.repository;

import com.coduk.duksungmap.domain.qna.entity.QnaThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaThreadRepository extends JpaRepository<QnaThread, Long> {

    // 질문 전체 조회
    List<QnaThread> findAllByOrderByCreatedAtDesc();
}
package com.coduk.duksungmap.domain.qna.repository;

import com.coduk.duksungmap.domain.qna.entity.QnaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaMessageRepository extends JpaRepository<QnaMessage, Long> {
    List<QnaMessage> findByThreadIdOrderByCreatedAtAsc(Long threadId);}
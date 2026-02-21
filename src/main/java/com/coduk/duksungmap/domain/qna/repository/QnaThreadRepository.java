package com.coduk.duksungmap.domain.qna.repository;

import com.coduk.duksungmap.domain.qna.entity.QnaThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaThreadRepository extends JpaRepository<QnaThread, Long> {
    Page<QnaThread> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
package com.coduk.duksungmap.domain.qna.repository;

import com.coduk.duksungmap.domain.qna.entity.QnaThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QnaThreadRepository extends JpaRepository<QnaThread, Long> {

    // 질문 전체 조회
    @Query("""
        select t from QnaThread t
        join fetch t.user
        order by t.createdAt desc
    """)
    List<QnaThread> findAllWithUserOrderByCreatedAtDesc();
}
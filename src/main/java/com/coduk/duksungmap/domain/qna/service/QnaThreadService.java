package com.coduk.duksungmap.domain.qna.service;

import com.coduk.duksungmap.domain.qna.dto.*;
import com.coduk.duksungmap.domain.qna.entity.QnaThread;
import com.coduk.duksungmap.domain.qna.exception.QnaErrorCode;
import com.coduk.duksungmap.domain.qna.repository.QnaMessageRepository;
import com.coduk.duksungmap.domain.qna.repository.QnaThreadRepository;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.domain.user.exception.UserErrorCode;
import com.coduk.duksungmap.domain.user.repository.UserRepository;
import com.coduk.duksungmap.global.common.enums.UserStatus;
import com.coduk.duksungmap.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaThreadService {

    private final QnaThreadRepository threadRepository;
    private final QnaMessageRepository messageRepository;
    private final UserRepository userRepository;

    // 질문 전체 조회
    @Transactional(readOnly = true)
    public QnaThreadListResponse getThreads() {
        List<QnaThreadListItem> items = threadRepository.findAllWithUserOrderByCreatedAtDesc()
                .stream()
                .map(t -> new QnaThreadListItem(
                        t.getId(),
                        t.getUser().getId(),
                        t.getContent(),
                        t.isAnswered(),
                        t.getCreatedAt()
                ))
                .toList();

        return new QnaThreadListResponse(items);
    }

    // 질문 상세 조회
    @Transactional(readOnly = true)
    public QnaThreadDetailResponse getThreadDetail(Long threadId) {
        QnaThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new CustomException(QnaErrorCode.THREAD_NOT_FOUND));

        QnaThreadDetailResponse.AnswerResponse answer = messageRepository
                .findFirstByThreadIdOrderByCreatedAtAsc(threadId)
                .map(m -> new QnaThreadDetailResponse.AnswerResponse(
                        m.getId(),
                        m.getUser().getId(), // adminId
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .orElse(null);

        return new QnaThreadDetailResponse(
                thread.getId(),
                thread.getUser().getId(),
                thread.getContent(),
                thread.getCreatedAt(),
                thread.isAnswered(),
                answer
        );
    }

    // 질문 작성
    @Transactional
    public CreateQnaThreadResponse createThread(Long userId, CreateQnaThreadRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new CustomException(UserErrorCode.USER_DELETED);
        }
        if (req.content() == null || req.content().isBlank()) {
            throw new CustomException(QnaErrorCode.CONTENT_EMPTY);
        }

        QnaThread saved = threadRepository.save(QnaThread.of(user, req.content()));
        return new CreateQnaThreadResponse(saved.getId());
    }

    // 질문 삭제 (관리자만 가능)
    @Transactional
    public void deleteThreadByAdmin(Long adminUserId, Long threadId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!admin.isAdmin()) throw new CustomException(QnaErrorCode.ADMIN_ONLY);

        QnaThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new CustomException(QnaErrorCode.THREAD_NOT_FOUND));

        thread.delete();
    }
}
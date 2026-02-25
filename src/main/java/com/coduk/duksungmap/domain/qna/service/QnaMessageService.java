package com.coduk.duksungmap.domain.qna.service;

import com.coduk.duksungmap.domain.qna.dto.CreateQnaMessageRequest;
import com.coduk.duksungmap.domain.qna.dto.CreateQnaMessageResponse;
import com.coduk.duksungmap.domain.qna.dto.UpdateQnaMessageRequest;
import com.coduk.duksungmap.domain.qna.dto.UpdateQnaMessageResponse;
import com.coduk.duksungmap.domain.qna.entity.QnaMessage;
import com.coduk.duksungmap.domain.qna.entity.QnaThread;
import com.coduk.duksungmap.domain.qna.exception.QnaErrorCode;
import com.coduk.duksungmap.domain.qna.repository.QnaMessageRepository;
import com.coduk.duksungmap.domain.qna.repository.QnaThreadRepository;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.domain.user.exception.UserErrorCode;
import com.coduk.duksungmap.domain.user.repository.UserRepository;
import com.coduk.duksungmap.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaMessageService {

    private final QnaThreadRepository threadRepository;
    private final QnaMessageRepository messageRepository;
    private final UserRepository userRepository;

    // 답변 작성
    @Transactional
    public CreateQnaMessageResponse createAnswer(Long adminUserId, Long threadId, CreateQnaMessageRequest req) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!admin.isAdmin()) throw new CustomException(QnaErrorCode.ADMIN_ONLY);

        QnaThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new CustomException(QnaErrorCode.THREAD_NOT_FOUND));

        if (req.content() == null || req.content().isBlank()) {
            throw new CustomException(QnaErrorCode.CONTENT_EMPTY);
        }

        boolean exists = messageRepository.findFirstByThreadIdOrderByCreatedAtAsc(threadId).isPresent();
        if (exists) throw new CustomException(QnaErrorCode.ANSWER_ALREADY_EXISTS);

        QnaMessage saved = messageRepository.save(QnaMessage.of(thread, admin, req.content()));
        thread.markAnswered();

        return new CreateQnaMessageResponse(saved.getId());
    }

    // 답변 수정
    @Transactional
    public UpdateQnaMessageResponse updateAnswer(Long adminUserId, Long messageId, UpdateQnaMessageRequest req) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!admin.isAdmin()) throw new CustomException(QnaErrorCode.ADMIN_ONLY);

        if (req.content() == null || req.content().isBlank()) {
            throw new CustomException(QnaErrorCode.CONTENT_EMPTY);
        }

        QnaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(QnaErrorCode.ANSWER_NOT_FOUND));

        message.updateContent(req.content());

        return new UpdateQnaMessageResponse(message.getId());
    }

    // 답변 삭제
    @Transactional
    public void deleteAnswer(Long adminUserId, Long messageId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (!admin.isAdmin()) throw new CustomException(QnaErrorCode.ADMIN_ONLY);

        QnaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(QnaErrorCode.ANSWER_NOT_FOUND));

        message.delete();
        message.getThread().unmarkAnswered(); // 삭제 시 답변 완료 체크도 삭제
    }
}
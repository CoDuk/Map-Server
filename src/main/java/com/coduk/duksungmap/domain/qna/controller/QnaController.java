package com.coduk.duksungmap.domain.qna.controller;

import com.coduk.duksungmap.domain.qna.dto.*;
import com.coduk.duksungmap.domain.qna.exception.QnaErrorCode;
import com.coduk.duksungmap.domain.qna.service.QnaMessageService;
import com.coduk.duksungmap.domain.qna.service.QnaThreadService;
import com.coduk.duksungmap.global.exception.CustomException;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import com.coduk.duksungmap.global.security.SecurityUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaThreadService threadService;
    private final QnaMessageService messageService;

    private SecurityUserPrincipal principalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof SecurityUserPrincipal p)) {
            throw new CustomException(QnaErrorCode.ADMIN_ONLY);
        }
        return p;
    }

    private Long currentUserId() {
        return principalOrThrow().userId();
    }

    private void adminOnly() {
        if (!principalOrThrow().isAdmin()) {
            throw new CustomException(QnaErrorCode.ADMIN_ONLY);
        }
    }

    // 질문 전체 조회 (로그인 필요)
    @GetMapping("/threads")
    public ResponseEntity<ApiResponse<QnaThreadListResponse>> listThreads() {
        QnaThreadListResponse result = threadService.getThreads();
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 질문 상세 조회 (로그인 필요)
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<QnaThreadDetailResponse>> getThread(@PathVariable Long threadId) {
        QnaThreadDetailResponse result = threadService.getThreadDetail(threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 질문 작성 (로그인 필요)
    @PostMapping("/threads")
    public ResponseEntity<ApiResponse<CreateQnaThreadResponse>> createThread(
            @RequestBody @Valid CreateQnaThreadRequest req
    ) {
        CreateQnaThreadResponse result = threadService.createThread(currentUserId(), req);
        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }

    // 질문 삭제 (관리자만)
    @DeleteMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@PathVariable Long threadId) {
        adminOnly();
        threadService.deleteThreadByAdmin(currentUserId(), threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    // 답변 등록 (관리자만)
    @PostMapping("/threads/{threadId}/answer")
    public ResponseEntity<ApiResponse<CreateQnaMessageResponse>> createAnswer(
            @PathVariable Long threadId,
            @RequestBody @Valid CreateQnaMessageRequest req
    ) {
        adminOnly();
        CreateQnaMessageResponse result = messageService.createAnswer(currentUserId(), threadId, req);
        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }

    // 답변 수정 (관리자만)
    @PatchMapping("/answers/{messageId}")
    public ResponseEntity<ApiResponse<UpdateQnaMessageResponse>> updateAnswer(
            @PathVariable Long messageId,
            @RequestBody @Valid UpdateQnaMessageRequest req
    ) {
        adminOnly();
        UpdateQnaMessageResponse result = messageService.updateAnswer(currentUserId(), messageId, req);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 답변 삭제 (관리자만)
    @DeleteMapping("/answers/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable Long messageId) {
        adminOnly();
        messageService.deleteAnswer(currentUserId(), messageId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }
}
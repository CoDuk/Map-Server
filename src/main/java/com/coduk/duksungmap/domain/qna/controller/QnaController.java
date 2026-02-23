package com.coduk.duksungmap.domain.qna.controller;

import com.coduk.duksungmap.domain.qna.dto.*;
import com.coduk.duksungmap.domain.qna.service.QnaMessageService;
import com.coduk.duksungmap.domain.qna.service.QnaThreadService;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaThreadService threadService;
    private final QnaMessageService messageService;

    // TODO: 로그인 유저 id 뽑아오는 유틸/어노테이션으로 교체 (현재: 헤더로 id 받음)
    private Long getUserId(String userIdHeader) {
        return Long.valueOf(userIdHeader);
    }

    // 질문 전체 조회
    @GetMapping("/threads")
    public ResponseEntity<ApiResponse<QnaThreadListResponse>> listThreads() {
        QnaThreadListResponse result = threadService.getThreads();
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 질문 상세 조회
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<QnaThreadDetailResponse>> getThread(@PathVariable Long threadId) {
        QnaThreadDetailResponse result = threadService.getThreadDetail(threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 로그인한 사용자만
    @PostMapping("/threads")
    public ResponseEntity<ApiResponse<CreateQnaThreadResponse>> createThread(
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestBody @Valid CreateQnaThreadRequest req
    ) {
        CreateQnaThreadResponse result = threadService.createThread(getUserId(userIdHeader), req);
        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }

    // 관리자만 질문 삭제
    @DeleteMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable Long threadId
    ) {
        threadService.deleteThreadByAdmin(getUserId(userIdHeader), threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    // 관리자만 답변 등록 (threadId 기준)
    @PostMapping("/threads/{threadId}/answer")
    public ResponseEntity<ApiResponse<CreateQnaMessageResponse>> createAnswer(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable Long threadId,
            @RequestBody @Valid CreateQnaMessageRequest req
    ) {
        CreateQnaMessageResponse result = messageService.createAnswer(getUserId(userIdHeader), threadId, req);
        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }

    // 관리자만 답변 수정 (messageId 기준)
    @PatchMapping("/answers/{messageId}")
    public ResponseEntity<ApiResponse<UpdateQnaMessageResponse>> updateAnswer(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable Long messageId,
            @RequestBody @Valid UpdateQnaMessageRequest req
    ) {
        UpdateQnaMessageResponse result = messageService.updateAnswer(getUserId(userIdHeader), messageId, req);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 관리자만 답변 삭제
    @DeleteMapping("/answers/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable Long messageId
    ) {
        messageService.deleteAnswer(getUserId(userIdHeader), messageId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }
}
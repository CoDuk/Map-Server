package com.coduk.duksungmap.domain.qna.controller;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.domain.qna.dto.*;
import com.coduk.duksungmap.domain.qna.exception.QnaErrorCode;
import com.coduk.duksungmap.domain.qna.service.QnaMessageService;
import com.coduk.duksungmap.domain.qna.service.QnaThreadService;
import com.coduk.duksungmap.global.exception.CustomException;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import com.coduk.duksungmap.global.security.SecurityUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "문의사항 API",
        description = "문의사항 질문/답변 기능 API입니다."
)
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {

    private final QnaThreadService threadService;
    private final QnaMessageService messageService;

    private SecurityUserPrincipal principalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof SecurityUserPrincipal p)) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
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
    @Operation(
            summary = "질문 목록 조회",
            description = """
                    질문 목록을 최신순으로 조회합니다.
                    - answered=true: 답변 완료(체크 아이콘 표시용)
                    - 로그인 필요
                    """
    )
    @GetMapping("/threads")
    public ResponseEntity<ApiResponse<QnaThreadListResponse>> listThreads() {
        QnaThreadListResponse result = threadService.getThreads();
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 질문 상세 조회 (로그인 필요)
    @Operation(
            summary = "질문 상세 조회",
            description = """
                    질문 1건과 답변(있다면 1개)을 함께 조회합니다.
                    - answer가 없으면 null
                    - 로그인 필요
                    """
    )
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<QnaThreadDetailResponse>> getThread(@PathVariable Long threadId) {
        QnaThreadDetailResponse result = threadService.getThreadDetail(threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    // 질문 작성 (로그인 필요)
    @Operation(
            summary = "질문 등록",
            description = """
                    로그인한 사용자가 질문을 등록합니다.
                    - 로그인 필요
                    """
    )
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
    @Operation(
            summary = "질문 삭제(관리자)",
            description = """
                    관리자가 질문을 삭제합니다(soft delete).
                    - 관리자 권한 필요
                    """
    )
    @DeleteMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@PathVariable Long threadId) {
        adminOnly();
        threadService.deleteThreadByAdmin(currentUserId(), threadId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    // 답변 등록 (관리자만)
    @Operation(
            summary = "답변 등록(관리자)",
            description = """
                    관리자가 질문에 답변을 등록합니다.
                    - 질문 1개당 답변은 1개만 가능
                    - 관리자 권한 필요
                    """
    )
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
    @Operation(
            summary = "답변 수정(관리자)",
            description = """
                    관리자가 답변 내용을 수정합니다.
                    - 관리자 권한 필요
                    """
    )
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
    @Operation(
            summary = "답변 삭제(관리자)",
            description = """
                    관리자가 답변을 삭제합니다(soft delete).
                    - 관리자 권한 필요
                    """
    )
    @DeleteMapping("/answers/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable Long messageId) {
        adminOnly();
        messageService.deleteAnswer(currentUserId(), messageId);
        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }
}
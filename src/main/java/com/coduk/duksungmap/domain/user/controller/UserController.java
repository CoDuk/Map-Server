package com.coduk.duksungmap.domain.user.controller;

import com.coduk.duksungmap.domain.user.service.UserService;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import com.coduk.duksungmap.global.security.SecurityUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "사용자 계정 API",
        description = "사용자 계정 관련 API입니다."
)
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private SecurityUserPrincipal principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUserPrincipal) auth.getPrincipal();
    }

    // 회원 탈퇴
    @Operation(
            summary = "회원 탈퇴",
            description = """
                    로그인한 사용자가 회원 탈퇴를 진행합니다.
                    
                    - 계정은 삭제되지 않고 status = DELETED로 변경됩니다.
                    - 모든 refresh token이 삭제되어 즉시 로그아웃 처리됩니다.
                    - 재가입 시 기존 데이터는 유지됩니다.
                    """
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletResponse response) {
        userService.withdraw(principal().userId(), response);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }
}
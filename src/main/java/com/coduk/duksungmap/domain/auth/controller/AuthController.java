package com.coduk.duksungmap.domain.auth.controller;

import com.coduk.duksungmap.domain.auth.dto.RefreshResponse;
import com.coduk.duksungmap.domain.auth.service.AuthService;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "인증/토큰 API",
        description = "Access Token 재발급 및 로그아웃을 처리하는 API입니다."
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.auth.refresh-cookie-name}")
    private String refreshCookieName;

    @Operation(
            summary = "Access Token 재발급",
            description = """
                Refresh Token 쿠키를 기반으로 새로운 Access Token을 발급합니다.
                - 유효한 Refresh Token이 존재해야 합니다.
                - 슬라이딩 세션 정책에 따라 Refresh Token의 만료 시간이 연장됩니다.
                """
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshRaw = extractCookie(request, refreshCookieName);
        String accessToken = authService.refresh(refreshRaw, response);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(new RefreshResponse(accessToken), SuccessCode.OK));
    }

    @Operation(
            summary = "로그아웃",
            description = """
                현재 디바이스의 Refresh Token을 삭제하고 쿠키를 제거합니다.
                - 쿠키에 저장된 Refresh Token을 기준으로 로그아웃 처리됩니다.
                """
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshRaw = extractCookie(request, refreshCookieName);

        authService.logout(refreshRaw, response);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
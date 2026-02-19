package com.coduk.duksungmap.domain.auth.controller;

import com.coduk.duksungmap.domain.auth.dto.RefreshResponse;
import com.coduk.duksungmap.domain.auth.service.AuthService;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.auth.refresh-cookie-name}")
    private String refreshCookieName;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(HttpServletRequest request) {

        String refreshRaw = extractCookie(request, refreshCookieName);
        String accessToken = authService.refresh(refreshRaw);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(new RefreshResponse(accessToken), SuccessCode.OK));
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
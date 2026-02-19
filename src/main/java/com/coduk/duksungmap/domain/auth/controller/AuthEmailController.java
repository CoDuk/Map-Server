package com.coduk.duksungmap.domain.auth.controller;

import com.coduk.duksungmap.domain.auth.dto.*;
import com.coduk.duksungmap.domain.auth.service.AuthEmailService;
import com.coduk.duksungmap.domain.auth.service.RefreshTokenService;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import com.coduk.duksungmap.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class AuthEmailController {

    private final AuthEmailService authEmailService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<SendCodeResponse>> sendCode(@RequestBody @Valid SendCodeRequest req) {
        long ttl = authEmailService.sendCode(req.duksungId());

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(new SendCodeResponse(ttl), SuccessCode.OK));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyCodeResponse>> verifyCode(
            @RequestBody @Valid VerifyCodeRequest req,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            HttpServletResponse response
    ) {
        User user = authEmailService.verifyCode(req.duksungId(), req.code());

        // access token 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.isAdmin());

        // refresh token 생성 + DB 저장
        String refreshRaw = refreshTokenService.issue(user, deviceId);

        // refresh token 쿠키 설정
        refreshTokenService.setRefreshCookie(response, refreshRaw);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(new VerifyCodeResponse(accessToken), SuccessCode.OK));
    }
}
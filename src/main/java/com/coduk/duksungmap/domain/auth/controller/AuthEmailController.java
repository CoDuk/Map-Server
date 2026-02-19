package com.coduk.duksungmap.domain.auth.controller;

import com.coduk.duksungmap.domain.auth.dto.*;
import com.coduk.duksungmap.domain.auth.service.AuthEmailService;
import com.coduk.duksungmap.domain.user.entity.User;
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
    // TODO: JwtProvider, RefreshTokenService 주입해서 아래 verify에서 사용

    @PostMapping("/send")
    public ResponseEntity<SendCodeResponse> sendCode(@RequestBody @Valid SendCodeRequest req) {
        long ttl = authEmailService.sendCode(req.duksungId());

        return ResponseEntity.ok(new SendCodeResponse(ttl));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody @Valid VerifyCodeRequest req,
                                                     HttpServletResponse response) {
        User user = authEmailService.verifyCode(req.duksungId(), req.code());

        // TODO: accessToken 생성

        // TODO: refreshToken 생성 + DB 저장 + 쿠키 설정

        return ResponseEntity.ok(new VerifyCodeResponse("TODO_ACCESS_TOKEN"));
    }
}
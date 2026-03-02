package com.coduk.duksungmap.domain.auth.service;

import com.coduk.duksungmap.domain.auth.entity.RefreshToken;
import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.domain.auth.repository.RefreshTokenRepository;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.global.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.refresh-ttl-days}")
    private long refreshTtlDays;

    @Value("${app.auth.refresh-cookie-name}")
    private String refreshCookieName;

    @Value("${app.auth.cookie-secure}")
    private boolean cookieSecure;

    @Transactional
    public String issue(User user, String deviceId) {
        String raw = UUID.randomUUID() + "." + UUID.randomUUID();
        String hash = hashOrThrow(raw);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(refreshTtlDays);

        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresentOrElse(
                        rt -> rt.rotate(hash, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.issue(user, hash, deviceId, expiresAt))
                );

        return raw; // 쿠키로 내려줄 원문
    }

    // refresh token 검증
    @Transactional
    public RefreshToken validateOrThrow(String refreshTokenRaw) {
        String hash = hashOrThrow(refreshTokenRaw);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new CustomException(AuthErrorCode.REFRESH_TOKEN_INVALID));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        return token;
    }

    @Transactional
    public void deleteOrThrow(String refreshTokenRaw) {
        String hash = hashOrThrow(refreshTokenRaw);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new CustomException(AuthErrorCode.REFRESH_TOKEN_INVALID));

        refreshTokenRepository.delete(token);
    }

    @Transactional
    public String rotate(String refreshTokenRaw, HttpServletResponse response) {
        RefreshToken token = validateOrThrow(refreshTokenRaw);

        // 새 raw/hash 생성
        String newRaw = UUID.randomUUID() + "." + UUID.randomUUID();
        String newHash = hashOrThrow(newRaw);

        // 만료 연장
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(refreshTtlDays);

        // 같은 row에서 교체
        token.rotate(newHash, newExpiresAt);

        // 새 쿠키 발급
        setRefreshCookie(response, newRaw);

        return newRaw;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        long seconds = refreshTtlDays * 24L * 60L * 60L;

        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshTokenRaw)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(seconds)
                .sameSite(cookieSecure ? "None" : "Lax") // prod는 None, local은 Lax
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSecure ? "None" : "Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 재발급/검증에서 사용
    public String hashOrThrow(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    // 특정 유저의 모든 토큰 삭제
    @Transactional
    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
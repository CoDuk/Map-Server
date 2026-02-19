package com.coduk.duksungmap.global.security;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTtlMinutes;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.auth.access-ttl-minutes}") long accessTtlMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMinutes = accessTtlMinutes;
    }

    public String createAccessToken(Long userId, boolean isAdmin) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtlMinutes * 60);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("isAdmin", isAdmin))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }
    }
}
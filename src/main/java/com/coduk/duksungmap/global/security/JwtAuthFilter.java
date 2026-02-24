package com.coduk.duksungmap.global.security;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.global.exception.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 없으면 그냥 통과 (permitAll 경로)
        if (auth == null || auth.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 형식만 허용
        if (!auth.startsWith("Bearer ")) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }

        String token = auth.substring(7).trim();
        if (token.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }

        Claims claims = jwtProvider.parseClaims(token);

        Long userId;
        try {
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }

        boolean isAdmin = false;
        Object isAdminClaim = claims.get("isAdmin");
        if (isAdminClaim instanceof Boolean b) isAdmin = b;
        else if (isAdminClaim instanceof String s) isAdmin = Boolean.parseBoolean(s);

        SecurityUserPrincipal principal = SecurityUserPrincipal.of(userId, isAdmin);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
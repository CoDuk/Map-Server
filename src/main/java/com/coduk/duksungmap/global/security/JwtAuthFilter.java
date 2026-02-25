package com.coduk.duksungmap.global.security;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 없으면 그냥 통과 (인증 필요한 경로면 Security가 401 처리)
        if (auth == null || auth.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 형식만 허용
        if (!auth.startsWith("Bearer ")) {
            write401(response, AuthErrorCode.ACCESS_TOKEN_INVALID);
            return;
        }

        String token = auth.substring(7).trim();
        if (token.isEmpty()) {
            write401(response, AuthErrorCode.ACCESS_TOKEN_INVALID);
            return;
        }

        try {
            Claims claims = jwtProvider.parseClaims(token);

            Long userId = Long.parseLong(claims.getSubject());

            boolean isAdmin = false;
            Object isAdminClaim = claims.get("isAdmin");
            if (isAdminClaim instanceof Boolean b) isAdmin = b;
            else if (isAdminClaim instanceof String s) isAdmin = Boolean.parseBoolean(s);

            SecurityUserPrincipal principal = SecurityUserPrincipal.of(userId, isAdmin);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            write401(response, AuthErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    private void write401(HttpServletResponse response, AuthErrorCode code) throws IOException {
        response.setStatus(code.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> body = ApiResponse.onFailure(code, null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
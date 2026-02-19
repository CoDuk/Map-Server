package com.coduk.duksungmap.domain.auth.service;

import com.coduk.duksungmap.domain.auth.entity.RefreshToken;
import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.domain.user.exception.UserErrorCode;
import com.coduk.duksungmap.domain.user.repository.UserRepository;
import com.coduk.duksungmap.global.common.enums.UserStatus;
import com.coduk.duksungmap.global.exception.CustomException;
import com.coduk.duksungmap.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public String refresh(String refreshRaw, HttpServletResponse response) {

        if (refreshRaw == null || refreshRaw.isBlank()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // refresh token 검증
        RefreshToken rt = refreshTokenService.validateOrThrow(refreshRaw);

        User user = userRepository.findById(rt.getUser().getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new CustomException(UserErrorCode.USER_DELETED);
        }

        refreshTokenService.rotate(refreshRaw, response);

        // access token 재발급
        return jwtProvider.createAccessToken(user.getId(), user.isAdmin());
    }

    @Transactional
    public void logout(String refreshRaw, HttpServletResponse response) {

        if (refreshRaw == null || refreshRaw.isBlank()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        refreshTokenService.deleteOrThrow(refreshRaw);
        refreshTokenService.clearRefreshCookie(response);
    }
}
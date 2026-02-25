package com.coduk.duksungmap.domain.user.service;

import com.coduk.duksungmap.domain.auth.service.RefreshTokenService;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.domain.user.exception.UserErrorCode;
import com.coduk.duksungmap.domain.user.repository.UserRepository;
import com.coduk.duksungmap.global.common.enums.UserStatus;
import com.coduk.duksungmap.global.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void withdraw(Long userId, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new CustomException(UserErrorCode.USER_DELETED);
        }

        // 유저 soft delete
        user.withdraw();

        // refresh token 전부 삭제 + 쿠키 제거
        refreshTokenService.deleteAllByUserId(userId);
        refreshTokenService.clearRefreshCookie(response);
    }
}
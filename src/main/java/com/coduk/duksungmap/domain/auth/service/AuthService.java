package com.coduk.duksungmap.domain.auth.service;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.domain.user.repository.UserRepository;
import com.coduk.duksungmap.global.common.enums.UserStatus;
import com.coduk.duksungmap.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final EmailVerificationStore store;
    private final UserRepository userRepository;

    @Value("${app.auth.email-domain}")
    private String emailDomain;

    @Value("${app.auth.verification-ttl-seconds}")
    private long ttlSeconds;

    private final SecureRandom random = new SecureRandom();

    public long sendCode(String duksungId) {
        String normalized = normalize(duksungId);
        String email = normalized + "@" + emailDomain;

        String code = generate6Digits();
        store.save(email, code, ttlSeconds);
        emailService.send(email, code);

        return ttlSeconds;
    }

    @Transactional
    public User verifyCode(String duksungId, String code) {
        String normalized = normalize(duksungId);
        String email = normalized + "@" + emailDomain;

        String saved = store.get(email);
        if (saved == null) throw new CustomException(AuthErrorCode.EMAIL_CODE_EXPIRED);
        if (!saved.equals(code)) throw new CustomException(AuthErrorCode.EMAIL_CODE_NOT_MATCH);

        store.delete(email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.getStatus() == UserStatus.DELETED) user.reactivate();
                    if (!user.isEmailVerified()) user.verifyEmail();
                    return user;
                })
                .orElseGet(() -> userRepository.save(User.createVerified(normalized, email)));
    }

    private String generate6Digits() {
        int n = random.nextInt(900000) + 100000;
        return String.valueOf(n);
    }

    private String normalize(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) throw new CustomException(AuthErrorCode.EMAIL_INVALID);
        if (s.contains("@") || s.contains(" ")) throw new CustomException(AuthErrorCode.EMAIL_INVALID);
        if (s.length() < 2 || s.length() > 50) throw new CustomException(AuthErrorCode.EMAIL_INVALID);
        return s;
    }
}
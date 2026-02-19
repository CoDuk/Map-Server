package com.coduk.duksungmap.domain.auth.entity;

import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static RefreshToken issue(User user, String tokenHash, String deviceId, LocalDateTime expiresAt) {
        RefreshToken rt = new RefreshToken();
        rt.user = user;
        rt.tokenHash = tokenHash;
        rt.deviceId = deviceId;
        rt.expiresAt = expiresAt;
        return rt;
    }
}
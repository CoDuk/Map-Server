package com.coduk.duksungmap.domain.user.entity;

import com.coduk.duksungmap.global.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name="uq_users_duksung_id", columnNames="duksung_id"),
                @UniqueConstraint(name="uq_users_email", columnNames="email")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="duksung_id", nullable=false, length=50)
    private String duksungId;

    @Column(name="email", nullable=false, length=200)
    private String email;

    @Column(name="email_verified", nullable=false)
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name="is_admin", nullable=false)
    private boolean isAdmin = false;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static User createVerified(String duksungId, String email) {
        User user = new User();
        user.duksungId = duksungId;
        user.email = email;
        user.emailVerified = true;
        user.status = UserStatus.ACTIVE;
        user.isAdmin = false;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }
}
package com.coduk.duksungmap.domain.user.repository;

import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.global.common.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmail(String email);
}
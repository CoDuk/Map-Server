package com.coduk.duksungmap.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationStore {

    private final StringRedisTemplate redisTemplate;

    public void save(String email, String code, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set("email:verify:" + email, code, ttlSeconds, TimeUnit.SECONDS);
    }

    public String get(String email) {
        return redisTemplate.opsForValue()
                .get("email:verify:" + email);
    }

    public void delete(String email) {
        redisTemplate.delete("email:verify:" + email);
    }
}
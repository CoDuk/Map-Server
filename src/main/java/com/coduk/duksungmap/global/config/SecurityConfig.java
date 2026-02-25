package com.coduk.duksungmap.global.config;

import com.coduk.duksungmap.global.security.JwtAuthFilter;
import com.coduk.duksungmap.global.security.JwtProvider;
import com.coduk.duksungmap.global.security.SecurityExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtProvider jwtProvider,
            ObjectMapper objectMapper,
            SecurityExceptionHandler securityExceptionHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(securityExceptionHandler) // 401
                        .accessDeniedHandler(securityExceptionHandler)      // 403
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // 관리자 전용 경로
                        .requestMatchers(HttpMethod.POST, "/api/qna/threads/*/answer").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/qna/answers/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/qna/answers/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/qna/threads/*").hasRole("ADMIN")
                        .requestMatchers("/api/qna/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider, objectMapper), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
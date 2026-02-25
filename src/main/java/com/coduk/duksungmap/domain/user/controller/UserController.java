package com.coduk.duksungmap.domain.user.controller;

import com.coduk.duksungmap.domain.user.service.UserService;
import com.coduk.duksungmap.global.response.ApiResponse;
import com.coduk.duksungmap.global.response.SuccessCode;
import com.coduk.duksungmap.global.security.SecurityUserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private SecurityUserPrincipal principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUserPrincipal) auth.getPrincipal();
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletResponse response) {
        userService.withdraw(principal().userId(), response);

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .body(ApiResponse.onSuccess(null, SuccessCode.OK));
    }
}
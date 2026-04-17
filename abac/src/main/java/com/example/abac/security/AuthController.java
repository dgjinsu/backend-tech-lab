package com.example.abac.security;

import com.example.abac.security.dto.LoginRequest;
import com.example.abac.security.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// /auth/** 경로는 SecurityConfig에서 permitAll 처리 → 인증 없이 타입 가능.
// 여기가 JWT를 '발급'하는 지점. 이후 모든 요청은 이 토큰을 Authorization 헤더로 가져와야 한다.
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // @Valid 실패 시 MethodArgumentNotValidException → GlobalExceptionHandler가 400 변환.
        return ResponseEntity.ok(authService.login(request));
    }
}

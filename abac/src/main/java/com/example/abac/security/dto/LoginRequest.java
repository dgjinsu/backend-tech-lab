package com.example.abac.security.dto;

import jakarta.validation.constraints.NotBlank;

// AuthController가 @Valid로 받아서 AuthService → AuthenticationManager까지 흘린다.
// @NotBlank는 null/공백 모두 거름 → 400 응답은 GlobalExceptionHandler가 처리.
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}

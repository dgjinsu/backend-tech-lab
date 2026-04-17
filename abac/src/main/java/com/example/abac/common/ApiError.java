package com.example.abac.common;

import java.time.LocalDateTime;

// GlobalExceptionHandler가 모든 예외 응답을 이 형태로 직렬화 → 클라이언트 입장에서
// 포맷이 일관되므로 상태 코드만 보고 어떤 필드가 있는지 추측할 필요가 없음.
public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    // 호출부에서 timestamp를 매번 쓰지 않도록 현재 시각을 자동 채움.
    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, LocalDateTime.now());
    }
}

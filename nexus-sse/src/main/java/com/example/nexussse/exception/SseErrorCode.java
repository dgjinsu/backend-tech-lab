package com.example.nexussse.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SseErrorCode {

    CONNECTION_NOT_FOUND("SSE_001", "연결된 클라이언트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SEND_FAILED("SSE_002", "메시지 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}

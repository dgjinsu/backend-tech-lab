package com.example.nexussse.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SseException.class)
    public ResponseEntity<Map<String, String>> handleSseException(SseException e) {
        log.error("SSE Error: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(Map.of(
                        "code", e.getErrorCode().getCode(),
                        "message", e.getErrorCode().getMessage()
                ));
    }
}

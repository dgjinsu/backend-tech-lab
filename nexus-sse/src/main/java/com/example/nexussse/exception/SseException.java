package com.example.nexussse.exception;

import lombok.Getter;

@Getter
public class SseException extends RuntimeException {

    private final SseErrorCode errorCode;

    public SseException(SseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

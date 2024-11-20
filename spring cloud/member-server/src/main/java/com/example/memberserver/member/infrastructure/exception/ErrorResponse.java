package com.example.memberserver.member.infrastructure.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponse {
    private ErrorCode errorName;
    private HttpStatus status;
    private String errorCode;
    private String errorMessage;
    private String errorLocation;
}
package com.example.memberserver.member.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberException extends RuntimeException {
    private final ErrorCode errorName;
    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorName = errorCode;
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getErrorMessage();
    }
}

package com.example.todoappserver.todo.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TodoAppException extends RuntimeException {
    private final ErrorCode errorName;
    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    public TodoAppException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorName = errorCode;
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getErrorMessage();
    }
}

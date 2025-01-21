package com.mirero.pwm.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            e.getErrorCode().name(),
            e.getErrorCode().getMessage()
        );
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }
}

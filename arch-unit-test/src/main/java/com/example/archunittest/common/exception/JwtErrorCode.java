package com.example.archunittest.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    INVALID_JWT(HttpStatus.BAD_REQUEST, "JWT Invalid");

    private final HttpStatus httpStatus;
    private final String message;
}

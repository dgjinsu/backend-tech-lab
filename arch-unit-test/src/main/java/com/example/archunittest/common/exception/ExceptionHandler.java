package com.example.archunittest.common.exception;

import com.example.archunittest.common.api.ApiResult;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
    public ApiResult handleCustomException(CustomException e) {
        return ApiResult.fail(e);
    }
}

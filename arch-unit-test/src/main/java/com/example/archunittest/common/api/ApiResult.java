package com.example.archunittest.common.api;

import com.example.archunittest.common.exception.CustomException;
import com.example.archunittest.common.exception.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResult<T>(
    @JsonIgnore HttpStatus httpStatus,
    @Nullable T data,
    String message,
    @Nullable PageInfo pageInfo,
    @Nullable ErrorResponse error
) {

    public static <T> ApiResult<T> ok(T data, String message) {
        return new ApiResult<>(HttpStatus.OK, data, message, null, null);
    }

    public static <T> ApiResult<T> withPaging(T data, String message, PageInfo pageInfo) {
        return new ApiResult<>(HttpStatus.OK, data, message, pageInfo, null);
    }

    public static <T> ApiResult<T> created(T data, String message) {
        return new ApiResult<>(HttpStatus.CREATED, data, message, null, null);
    }

    public static <T> ApiResult<T> created(String message) {
        return new ApiResult<>(HttpStatus.CREATED, null, message, null, null);
    }

    public static <T> ApiResult<T> fail(CustomException e) {
        return new ApiResult<>(
            e.getErrorCode().getHttpStatus(),
            null,
            null,
            null,
            new ErrorResponse(e.getErrorCode().name(), e.getErrorCode().getMessage())
        );
    }
}
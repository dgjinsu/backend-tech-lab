package com.example.archunittest.common.exception;

public record ErrorResponse(
    String errorName,
    String errorMessage
) {

}

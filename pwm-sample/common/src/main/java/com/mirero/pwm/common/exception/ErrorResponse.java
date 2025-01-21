package com.mirero.pwm.common.exception;

public record ErrorResponse(
    String errorName,
    String errorMessage
    ) {

}

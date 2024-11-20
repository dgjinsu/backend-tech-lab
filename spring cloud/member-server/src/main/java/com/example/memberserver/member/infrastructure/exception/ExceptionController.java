package com.example.memberserver.member.infrastructure.exception;

import com.example.memberserver.member.infrastructure.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<?> handleCustomException(MemberException e) {
        String errorLocation = logError(e);
        ErrorResponse errorResponse = buildErrorResponse(e.getErrorName(), e.getStatus(), e.getErrorCode(), e.getErrorMessage(), errorLocation);
        return ResponseEntity.status(e.getStatus()).body(new Response<>(errorResponse, "커스텀 예외 반환"));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleUnhandledException(Exception e) {
//        logError(e);
//        return ResponseEntity.badRequest().body(new Response<>(e.getMessage(), "핸들링하지 않은 예외 반환"));
//    }

    private String logError(Exception e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StackTraceElement[] stackTrace = e.getStackTrace();
        String errorLocation = "";
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            errorLocation = element.getClassName() + " at line " + element.getLineNumber();
        }

        log.error("에러 발생 시각: " + currentTime);
        log.error("에러 위치: " + errorLocation);
        log.error("에러 내용: " + e.getMessage());

        return errorLocation;
    }

    private ErrorResponse buildErrorResponse(ErrorCode errorName, HttpStatus status, String errorCode, String errorMessage, String errorLocation) {
        return ErrorResponse.builder()
                .errorName(errorName)
                .status(status)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorLocation(errorLocation)
                .build();
    }
}

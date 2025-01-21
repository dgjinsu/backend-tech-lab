package com.mirero.pwm.member.domain.exception;

import com.mirero.pwm.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

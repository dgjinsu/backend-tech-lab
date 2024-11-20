package com.example.memberserver.member.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "member-001", "Member 정보가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String errorMessage;
}

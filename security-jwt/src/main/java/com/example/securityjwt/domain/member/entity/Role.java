package com.example.securityjwt.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("일반 회원"),
    ADMIN("관리자");

    private final String description;
}

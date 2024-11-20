package com.example.common.role;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_MEMBER("사용자"),
    ROLE_ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
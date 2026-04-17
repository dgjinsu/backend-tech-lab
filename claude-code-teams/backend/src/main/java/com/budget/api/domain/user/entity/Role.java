package com.budget.api.domain.user.entity;

public enum Role {
    EMPLOYEE,
    MANAGER,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}

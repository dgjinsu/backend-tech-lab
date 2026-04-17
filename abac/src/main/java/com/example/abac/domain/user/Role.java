package com.example.abac.domain.user;

public enum Role {
    EMPLOYEE,
    MANAGER,
    FINANCE,
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}

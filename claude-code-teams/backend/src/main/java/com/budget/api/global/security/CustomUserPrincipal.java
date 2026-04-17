package com.budget.api.global.security;

import com.budget.api.domain.user.entity.Role;

public record CustomUserPrincipal(Long userId, Role role, Long departmentId) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }
}

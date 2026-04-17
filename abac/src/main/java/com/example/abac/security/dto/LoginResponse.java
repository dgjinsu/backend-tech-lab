package com.example.abac.security.dto;

import com.example.abac.domain.user.Role;

public record LoginResponse(
        String token,
        String username,
        Role role,
        Long departmentId
) {
}

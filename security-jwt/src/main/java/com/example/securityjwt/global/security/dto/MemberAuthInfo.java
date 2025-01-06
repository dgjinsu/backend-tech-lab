package com.example.securityjwt.global.security.dto;

import com.example.securityjwt.domain.member.entity.Role;

public record MemberAuthInfo(
    Long memberId,
    Role role
) {

}

package com.example.memberserver.member.adaptor.in.web.dto;

import com.example.common.role.Role;

public record MemberSaveRequest(String loginId, String password, String name, Role role) {
}

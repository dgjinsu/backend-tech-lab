package com.example.memberserver.member.infrastructure.security;

import com.example.common.role.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class MemberDto {
    private Long id;
    private String loginId;
    private Role role;
}

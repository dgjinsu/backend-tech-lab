package com.example.abac.security.dto;

import com.example.abac.domain.user.Role;

// token은 이후 요청의 Authorization 헤더용. role/departmentId는 UI가 메뉴 가시성 등을
// 판단할 때 편하게 쓰라고 같이 내려줄 뿐, 서버 권한 판정은 항상 토큰을 다시 파싱해서 한다.
public record LoginResponse(
        String token,
        String username,
        Role role,
        Long departmentId
) {
}

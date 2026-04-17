package com.example.abac.domain.user;

// [RBAC 단일 진실 소스] 네 가지 역할을 고정. 새 역할을 추가하면
// ExpenseSpecifications.visibleTo()의 switch, ExpensePolicy의 각 can* 메서드까지 함께 손봐야 한다.
public enum Role {
    EMPLOYEE,
    MANAGER,
    FINANCE,
    ADMIN;

    // Spring Security 관례: 권한 문자열이 "ROLE_" 로 시작해야 hasRole('X')가 매칭된다.
    // SimpleGrantedAuthority에 이 값을 넣으면 hasRole('MANAGER') / hasAuthority('ROLE_MANAGER') 둘 다 OK.
    public String authority() {
        return "ROLE_" + name();
    }
}

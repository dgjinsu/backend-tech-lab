package com.example.abac.security;

import com.example.abac.domain.user.Role;
import com.example.abac.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// [학습 포인트] Spring Security의 UserDetails를 우리 도메인에 맞춰 확장한 구현.
// userId/role/departmentId를 들고 다녀서 컨트롤러가 @AuthenticationPrincipal로 바로 꺼내 쓴다.
// = Principal 자체가 ABAC 판정에 필요한 '주체 속성'을 모두 품고 있는 셈.
@Getter
public class CustomUserPrincipal implements UserDetails {

    // userId: ownerId 비교용
    // role: RBAC + ABAC 판정의 주 스위치
    // departmentId: sameDept/데이터 스코프 필터용
    private final Long userId;
    private final String username;
    private final String password;
    private final Role role;
    private final Long departmentId;

    public CustomUserPrincipal(Long userId, String username, String password, Role role, Long departmentId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
    }

    // DB에서 꺼낸 User → Principal로 변환하는 유일한 입구.
    // CustomUserDetailsService가 이 팩토리를 쓴다.
    public static CustomUserPrincipal from(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getDepartmentId()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role enum 하나만 권한으로 부여. "ROLE_" 접두사는 Role.authority()가 붙여준다.
        // 여러 권한이 필요하면 여기에 SimpleGrantedAuthority를 추가하면 된다.
        return List.of(new SimpleGrantedAuthority(role.authority()));
    }

    // 아래 네 메서드는 계정 상태 플래그. 잠금/만료 기능을 도입하면 실제 User 컬럼과 연결.
    // 지금은 학습 범위 밖이라 모두 true로 고정 → 로그인 거부 사유를 '비밀번호 틀림'으로 좁힌다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

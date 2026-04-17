package com.example.abac.domain.user;

import com.example.abac.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// [학습 포인트] User는 RBAC의 role과 ABAC의 departmentId 두 종류 속성을 한 객체에 담고 있다.
// 인증 시 CustomUserPrincipal로 복사되고, 이후 JWT claim에도 그대로 올라가 SecurityContext에 살아남는다.
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // BCrypt 해시 문자열을 그대로 저장. 평문 저장 절대 금지.
    @Column(nullable = false)
    private String password;

    // [RBAC 축] 역할. @PreAuthorize("hasRole('MANAGER')") 같은 RBAC 표현식의 대조 대상.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // [ABAC 속성] 사용자 '소속' 부서. sameDept 판정에 쓰이고, Expense의 departmentId와 비교된다.
    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    public User(String username, String password, Role role, Long departmentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
    }
}

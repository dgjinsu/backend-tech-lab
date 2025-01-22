package com.mirero.pwm.member.persistence.entity;

import com.mirero.pwm.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, length = 12)
    private String loginId;

    @Column(name = "password", nullable = false, length = 15)
    private String password;

    @Builder
    public MemberEntity(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public static MemberEntity of(Member member) {
        return MemberEntity.builder()
            .loginId(member.getLoginId())
            .password(member.getPassword())
            .build();
    }

    public Member toMember() {
        return Member.builder()
            .memberId(this.getId())
            .loginId(this.getLoginId())
            .build();
    }
}

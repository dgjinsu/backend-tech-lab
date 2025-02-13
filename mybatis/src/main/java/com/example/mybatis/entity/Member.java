package com.example.mybatis.entity;

import com.example.mybatis.dto.SaveMemberRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER", schema = "MYBATIS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBERID", nullable = false)
    private Long memberId;

    @Column(name = "LOGINID", length = 100, unique = true)
    private String loginId;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "AGE")
    private Integer age;

    @Builder
    public Member(String loginId, String name, Integer age) {
        this.loginId = loginId;
        this.name = name;
        this.age = age;
    }

    public static Member from(SaveMemberRequest request) {
        return Member.builder()
            .loginId(request.loginId())
            .name(request.name())
            .age(request.age())
            .build();
    }
}

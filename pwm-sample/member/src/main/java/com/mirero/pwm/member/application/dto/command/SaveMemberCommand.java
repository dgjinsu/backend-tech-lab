package com.mirero.pwm.member.application.dto.command;

import com.mirero.pwm.member.domain.Member;
import com.mirero.pwm.member.infrastructure.controller.dto.SaveMemberRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveMemberCommand(
    @NotBlank(message = "아이디는 필수 값입니다.")
    @Size(min = 5, max = 20, message = "Login ID는 5자 이상, 20자 이하이어야 합니다.")
    String loginId,

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password
) {

    public static SaveMemberCommand from(SaveMemberRequest request) {
        return new SaveMemberCommand(
            request.loginId(),
            request.password()
        );
    }

    public Member toMember() {
        return Member.builder()
            .loginId(this.loginId)
            .password(this.password)
            .build();
    }
}

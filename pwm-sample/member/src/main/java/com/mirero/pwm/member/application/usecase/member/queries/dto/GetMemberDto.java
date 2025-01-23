package com.mirero.pwm.member.application.usecase.member.queries.dto;

import com.mirero.pwm.member.domain.Member;

public record GetMemberDto(
    Long memberId,
    String loginId
) {

    public static GetMemberDto from(Member member) {
        return new GetMemberDto(
            member.getMemberId(),
            member.getLoginId()
        );
    }
}

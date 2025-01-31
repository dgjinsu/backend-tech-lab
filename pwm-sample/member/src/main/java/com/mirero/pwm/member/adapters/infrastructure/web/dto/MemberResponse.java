package com.mirero.pwm.member.adapters.infrastructure.web.dto;

import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;

public record MemberResponse(
    Long memberId,
    String loginId
) {

    public static MemberResponse from(GetMemberDto query) {
        return new MemberResponse(
            query.memberId(),
            query.loginId()
        );
    }
}

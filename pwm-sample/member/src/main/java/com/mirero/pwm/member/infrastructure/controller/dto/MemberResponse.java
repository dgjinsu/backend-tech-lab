package com.mirero.pwm.member.infrastructure.controller.dto;

import com.mirero.pwm.member.application.dto.query.MemberQuery;

public record MemberResponse(
    Long memberId,
    String loginId
) {

    public static MemberResponse from(MemberQuery query) {
        return new MemberResponse(
            query.memberId(),
            query.loginId()
        );
    }
}

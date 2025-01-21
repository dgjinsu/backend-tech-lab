package com.mirero.pwm.member.application.dto.query;

import com.mirero.pwm.member.domain.Member;

public record MemberQuery(
    Long memberId,
    String loginId
) {

    public static MemberQuery from(Member member) {
        return new MemberQuery(
            member.getMemberId(),
            member.getLoginId()
        );
    }
}

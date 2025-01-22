package com.mirero.pwm.member.application.port;

import com.mirero.pwm.member.domain.Member;

public interface GetMemberInfoPort {

    Member getMemberInfo(Long memberId);
}

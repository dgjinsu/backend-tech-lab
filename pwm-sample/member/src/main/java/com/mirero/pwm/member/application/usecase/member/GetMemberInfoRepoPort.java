package com.mirero.pwm.member.application.usecase.member;

import com.mirero.pwm.member.domain.Member;

public interface GetMemberInfoRepoPort {

    Member getMemberInfo(Long memberId);
}

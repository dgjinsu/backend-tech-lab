package com.mirero.pwm.member.application.usecase.member;

import com.mirero.pwm.member.domain.Member;

public interface SaveMemberRepoPort {

    Member saveMember(Member member);
}

package com.mirero.pwm.member.application.port;

import com.mirero.pwm.member.domain.Member;

public interface SaveMemberPort {

    Member saveMember(Member member);
}

package com.mirero.pwm.member.application.usecase.member.queries;

import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;

public interface GetMemberQuery {
    GetMemberDto getMemberInfo(Long memberId);
}

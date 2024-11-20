package com.example.memberserver.member.application.port.in;

import com.example.memberserver.member.adaptor.in.web.dto.MemberInfoResponse;

public interface GetMemberInfoQuery {
    MemberInfoResponse getMemberInfo(Long memberId);
}

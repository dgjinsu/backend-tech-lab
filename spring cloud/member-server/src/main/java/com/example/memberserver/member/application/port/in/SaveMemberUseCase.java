package com.example.memberserver.member.application.port.in;

import com.example.memberserver.member.adaptor.in.web.dto.MemberResponse;
import com.example.memberserver.member.adaptor.in.web.dto.MemberSaveRequest;

public interface SaveMemberUseCase {
    MemberResponse saveMember(MemberSaveRequest request);
}

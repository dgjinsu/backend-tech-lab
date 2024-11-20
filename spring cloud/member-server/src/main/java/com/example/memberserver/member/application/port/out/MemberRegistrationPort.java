package com.example.memberserver.member.application.port.out;

import com.example.memberserver.member.adaptor.out.persistence.MemberEntity;
import com.example.memberserver.member.domain.Member;

public interface MemberRegistrationPort {

    Member saveMember(Member member);
}

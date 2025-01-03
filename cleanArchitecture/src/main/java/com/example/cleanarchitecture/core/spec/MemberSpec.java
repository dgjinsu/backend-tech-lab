package com.example.cleanarchitecture.core.spec;

import com.example.cleanarchitecture.core.domain.Member;
import com.example.cleanarchitecture.core.dto.query.MemberListQuery;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;

public interface MemberSpec {

    MemberQuery saveMember(Member member);

    MemberListQuery findAllMembers();
}

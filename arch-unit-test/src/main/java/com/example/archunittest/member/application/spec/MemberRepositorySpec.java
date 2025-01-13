package com.example.archunittest.member.application.spec;

import com.example.archunittest.member.domain.Member;
import com.example.archunittest.member.persistence.entity.MemberEntity;

public interface MemberRepositorySpec {

    Member saveMember(Member member);
}

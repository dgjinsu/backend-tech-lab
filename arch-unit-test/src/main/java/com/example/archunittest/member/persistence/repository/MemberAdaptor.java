package com.example.archunittest.member.persistence.repository;

import com.example.archunittest.member.application.spec.MemberRepositorySpec;
import com.example.archunittest.member.domain.Member;
import com.example.archunittest.member.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptor implements MemberRepositorySpec {

    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        MemberEntity entity = memberRepository.save(
            MemberEntity.builder().name(member.getName()).build());

        return Member.builder().id(entity.getId()).name(entity.getName()).build();
    }
}

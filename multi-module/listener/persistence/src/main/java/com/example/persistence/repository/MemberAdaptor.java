package com.example.persistence.repository;

import com.example.application.spec.MemberRepositorySpec;
import com.example.domain.member.Member;
import com.example.persistence.entity.MemberEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptor implements MemberRepositorySpec {

    private final MemberRepository memberRepository;

    @Override
    public void saveMember(Member member) {
        memberRepository.save(
            MemberEntity.builder()
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .build()
        );
    }

    @Override
    public List<Member> getMembers() {
        List<MemberEntity> memberEntities = memberRepository.findAll();
        return memberEntities.stream()
            .map(entity -> Member.builder()
                .id(entity.getMemberId())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .build())
            .toList();

    }
}

package com.example.cleanarchitecture.infrastructures.persistence.repository;

import com.example.cleanarchitecture.core.domain.Member;
import com.example.cleanarchitecture.core.dto.query.MemberListQuery;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;
import com.example.cleanarchitecture.core.spec.MemberSpec;
import com.example.cleanarchitecture.infrastructures.persistence.entity.MemberEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptor implements MemberSpec {
    private final MemberRepository memberRepository;


    @Override
    public MemberQuery saveMember(Member member) {
        MemberEntity memberEntity = memberRepository.save(
            new MemberEntity(member.getLoginId(), member.getPassword())
        );

        return new MemberQuery(
            memberEntity.getId(),
            memberEntity.getLoginId(),
            memberEntity.getPassword()
        );
    }

    @Override
    public MemberListQuery findAllMembers() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();

        return new MemberListQuery(
            memberEntityList.stream()
                .map(memberEntity -> new MemberListQuery.MemberQuery(
                    memberEntity.getId(),
                    memberEntity.getLoginId(),
                    memberEntity.getPassword()
                ))
                .toList()
        );
    }
}

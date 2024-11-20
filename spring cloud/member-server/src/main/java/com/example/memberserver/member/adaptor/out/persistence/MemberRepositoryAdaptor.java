package com.example.memberserver.member.adaptor.out.persistence;

import com.example.memberserver.common.annotation.PersistenceAdapter;
import com.example.memberserver.member.adaptor.out.mapper.MemberEntityMapper;
import com.example.memberserver.member.application.port.out.MemberInfoPort;
import com.example.memberserver.member.application.port.out.MemberRegistrationPort;
import com.example.memberserver.member.domain.Member;
import com.example.memberserver.member.infrastructure.exception.ErrorCode;
import com.example.memberserver.member.infrastructure.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor.MemberInfo;

@RequiredArgsConstructor
@PersistenceAdapter
public class MemberRepositoryAdaptor implements MemberInfoPort,
    MemberRegistrationPort {

    private final MemberRepository memberRepository;
    private final MemberEntityMapper mapper;

    @Override
    public Member saveMember(Member member) {
        MemberEntity memberEntity = memberRepository.save(mapper.toMemberEntity(member));

        return mapper.toMember(memberEntity);
    }

    @Override
    public MemberInfo getMemberInfo() {
        return null;
    }

    @Override
    public Member findById(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        return mapper.toMember(memberEntity);
    }

    @Override
    public Member findByLoginId(String loginId) {
        MemberEntity memberEntity = memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        return mapper.toMember(memberEntity);
    }
}

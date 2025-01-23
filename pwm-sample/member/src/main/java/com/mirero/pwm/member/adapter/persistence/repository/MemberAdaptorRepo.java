package com.mirero.pwm.member.adapter.persistence.repository;

import com.mirero.pwm.member.application.usecase.member.GetMemberInfoRepoPort;
import com.mirero.pwm.member.application.usecase.member.SaveMemberRepoPort;
import com.mirero.pwm.member.domain.Member;
import com.mirero.pwm.member.domain.exception.MemberErrorCode;
import com.mirero.pwm.member.domain.exception.MemberException;
import com.mirero.pwm.member.adapter.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptorRepo implements SaveMemberRepoPort, GetMemberInfoRepoPort {

    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        MemberEntity memberEntity = memberRepository.save(MemberEntity.from(member));

        return memberEntity.toMember();
    }

    @Override
    public Member getMemberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return memberEntity.toMember();
    }
}

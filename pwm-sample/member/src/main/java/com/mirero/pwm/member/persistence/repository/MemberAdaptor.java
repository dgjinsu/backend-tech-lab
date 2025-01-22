package com.mirero.pwm.member.persistence.repository;

import com.mirero.pwm.member.application.port.GetMemberInfoPort;
import com.mirero.pwm.member.application.port.SaveMemberPort;
import com.mirero.pwm.member.domain.Member;
import com.mirero.pwm.member.domain.exception.MemberErrorCode;
import com.mirero.pwm.member.domain.exception.MemberException;
import com.mirero.pwm.member.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptor implements SaveMemberPort, GetMemberInfoPort {

    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        MemberEntity memberEntity = memberRepository.save(MemberEntity.of(member));

        return memberEntity.toMember();
    }

    @Override
    public Member getMemberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return memberEntity.toMember();
    }
}

package com.mirero.pwm.member.persistence.repository;

import com.mirero.pwm.member.application.port.SaveMemberPort;
import com.mirero.pwm.member.domain.Member;
import com.mirero.pwm.member.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAdaptor implements SaveMemberPort {

    private final MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        MemberEntity memberEntity = memberRepository.save(MemberEntity.of(member));

        return Member.builder()
            .memberId(memberEntity.getId())
            .loginId(memberEntity.getLoginId())
            .password(memberEntity.getPassword())
            .build();
    }
}

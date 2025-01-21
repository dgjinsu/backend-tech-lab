package com.mirero.pwm.member.application.service;

import com.mirero.pwm.member.application.dto.command.SaveMemberCommand;
import com.mirero.pwm.member.application.dto.query.MemberQuery;
import com.mirero.pwm.member.application.port.SaveMemberPort;
import com.mirero.pwm.member.application.usecase.SaveMemberUseCase;
import com.mirero.pwm.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService implements SaveMemberUseCase {

    private final SaveMemberPort saveMemberPort;

    @Override
    public MemberQuery saveMember(SaveMemberCommand command) {
        Member member = saveMemberPort.saveMember(command.toMember());
        return MemberQuery.from(member);
    }
}

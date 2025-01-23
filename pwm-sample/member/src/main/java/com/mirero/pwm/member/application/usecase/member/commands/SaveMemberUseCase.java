package com.mirero.pwm.member.application.usecase.member.commands;

import com.mirero.pwm.member.application.usecase.member.commands.dto.SaveMemberDto;
import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;
import com.mirero.pwm.member.application.usecase.member.SaveMemberRepoPort;
import com.mirero.pwm.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SaveMemberUseCase implements SaveMemberCommand {

    private final SaveMemberRepoPort saveMemberRepoPort;

    @Override
    public GetMemberDto saveMember(SaveMemberDto dto) {
        Member member = saveMemberRepoPort.saveMember(dto.toMember());
        return GetMemberDto.from(member);
    }
}

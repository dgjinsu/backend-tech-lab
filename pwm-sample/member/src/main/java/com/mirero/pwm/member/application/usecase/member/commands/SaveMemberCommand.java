package com.mirero.pwm.member.application.usecase.member.commands;

import com.mirero.pwm.member.application.usecase.member.commands.dto.SaveMemberDto;
import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;

public interface SaveMemberCommand {

    GetMemberDto saveMember(SaveMemberDto dto);
}

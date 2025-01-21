package com.mirero.pwm.member.application.usecase;

import com.mirero.pwm.member.application.dto.command.SaveMemberCommand;
import com.mirero.pwm.member.application.dto.query.MemberQuery;

public interface SaveMemberUseCase {

    MemberQuery saveMember(SaveMemberCommand command);
}

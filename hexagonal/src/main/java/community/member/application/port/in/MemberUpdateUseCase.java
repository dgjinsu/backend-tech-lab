package community.member.application.port.in;

import community.member.application.port.in.command.MemberUpdateCommand;
import community.member.application.port.in.result.MemberUpdateResult;

public interface MemberUpdateUseCase {
    MemberUpdateResult updateMember(MemberUpdateCommand command);
}

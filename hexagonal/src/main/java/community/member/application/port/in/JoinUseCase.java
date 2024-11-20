package community.member.application.port.in;

import community.member.application.port.in.command.JoinCommand;

public interface JoinUseCase {
    Long joinMember(JoinCommand command);
}

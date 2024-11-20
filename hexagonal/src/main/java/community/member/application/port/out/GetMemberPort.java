package community.member.application.port.out;

import community.member.domain.Member;

public interface GetMemberPort {
    Member getMember(Long memberId);
}

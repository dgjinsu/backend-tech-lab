package community.member.application.port.out;

import community.member.domain.Member;

public interface SaveMemberPort {
    Long saveMember(Member member);
}

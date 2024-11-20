package community.member.application.port.out;

import community.member.domain.Member;

public interface UpdateMemberPort {
    void updateMember(Member member);
}

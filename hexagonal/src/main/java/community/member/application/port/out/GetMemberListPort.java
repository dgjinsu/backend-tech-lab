package community.member.application.port.out;

import community.member.application.port.in.result.MemberResult;
import java.util.List;

public interface GetMemberListPort {
    List<MemberResult> getMemberList();
}

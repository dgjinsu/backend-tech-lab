package community.member.application.port.in;

import community.member.application.port.in.result.MemberResult;
import java.util.List;

public interface MemberListQuery {
    List<MemberResult> getMemberList();
}

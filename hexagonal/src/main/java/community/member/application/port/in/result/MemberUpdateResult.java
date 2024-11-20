package community.member.application.port.in.result;

import community.member.domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberUpdateResult {
    private Long memberId;
    private String password;
    private String name;

    public static MemberUpdateResult from(Member member) {
        return MemberUpdateResult.builder()
            .memberId(member.getMemberId())
            .password(member.getPassword())
            .name(member.getName())
            .build();
    }
}

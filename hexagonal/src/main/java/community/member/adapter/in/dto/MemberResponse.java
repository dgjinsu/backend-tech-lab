package community.member.adapter.in.dto;

import community.member.application.port.in.result.MemberResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponse {

    private Long memberId;
    private String name;

    public static MemberResponse from(MemberResult memberResult) {
        return MemberResponse.builder()
            .memberId(memberResult.getMemberId())
            .name(memberResult.getName())
            .build();
    }
}

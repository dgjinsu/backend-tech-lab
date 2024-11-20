package community.member.application.port.in.result;

import community.member.adapter.out.entity.MemberEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResult {

    private Long memberId;
    private String name;

    public static MemberResult from(MemberEntity memberEntity) {
        return MemberResult.builder()
            .memberId(memberEntity.getId())
            .name(memberEntity.getName())
            .build();
    }
}

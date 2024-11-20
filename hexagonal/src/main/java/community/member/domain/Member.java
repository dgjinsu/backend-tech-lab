package community.member.domain;

import community.member.application.port.in.command.MemberUpdateCommand;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member {
    private Long memberId;
    private String loginId;
    private String password;
    private String name;

    public void updateMember(MemberUpdateCommand command) {
        this.password = command.getPassword();
        this.name = command.getName();
    }
}

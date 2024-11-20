package community.member.adapter.in.dto;

import lombok.Data;

@Data
public class JoinRequest {
    private String loginId;
    private String password;
    private String name;
}

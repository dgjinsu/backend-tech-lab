package com.example.memberserver.member.adaptor.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class LoginRequest {
    private String loginId;
    private String password;
}

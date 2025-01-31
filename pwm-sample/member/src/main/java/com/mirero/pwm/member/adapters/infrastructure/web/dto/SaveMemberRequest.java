package com.mirero.pwm.member.adapters.infrastructure.web.dto;

public record SaveMemberRequest(
    String loginId,
    String password
) {

}

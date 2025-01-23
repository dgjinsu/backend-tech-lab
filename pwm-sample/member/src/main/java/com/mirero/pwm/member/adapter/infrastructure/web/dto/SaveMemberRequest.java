package com.mirero.pwm.member.adapter.infrastructure.web.dto;

public record SaveMemberRequest(
    String loginId,
    String password
) {

}

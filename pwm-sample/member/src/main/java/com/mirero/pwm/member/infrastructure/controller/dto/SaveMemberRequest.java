package com.mirero.pwm.member.infrastructure.controller.dto;

public record SaveMemberRequest(
    String loginId,
    String password
) {

}

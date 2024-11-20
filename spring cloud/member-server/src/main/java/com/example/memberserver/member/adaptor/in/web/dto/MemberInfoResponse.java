package com.example.memberserver.member.adaptor.in.web.dto;

import lombok.Builder;

@Builder
public record MemberInfoResponse(
    String memberId,
    String name) {
}

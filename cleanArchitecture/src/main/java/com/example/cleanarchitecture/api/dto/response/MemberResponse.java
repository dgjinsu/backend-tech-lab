package com.example.cleanarchitecture.api.dto.response;

public record MemberResponse(
    Long memberId,
    String loginId,
    String password
) {

}

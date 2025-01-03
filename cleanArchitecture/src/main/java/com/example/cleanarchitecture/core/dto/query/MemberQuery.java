package com.example.cleanarchitecture.core.dto.query;

public record MemberQuery(
    Long memberId,
    String loginId,
    String password
) {

}

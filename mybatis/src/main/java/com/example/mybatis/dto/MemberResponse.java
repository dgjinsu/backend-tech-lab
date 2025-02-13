package com.example.mybatis.dto;

public record MemberResponse(
    Long memberId,
    String loginId,
    String name,
    Integer age
) {

}

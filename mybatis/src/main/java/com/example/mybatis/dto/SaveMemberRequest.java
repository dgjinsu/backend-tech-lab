package com.example.mybatis.dto;

public record SaveMemberRequest(
    String loginId,
    String name,
    Integer age
) {

}

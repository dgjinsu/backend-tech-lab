package com.example.cleanarchitecture.api.dto.request;

public record SaveMemberRequest(
    String loginId,
    String password
) {

}

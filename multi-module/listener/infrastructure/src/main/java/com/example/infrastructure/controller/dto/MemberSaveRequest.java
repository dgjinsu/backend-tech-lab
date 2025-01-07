package com.example.infrastructure.controller.dto;

public record MemberSaveRequest(
    String loginId,
    String password
) {

}

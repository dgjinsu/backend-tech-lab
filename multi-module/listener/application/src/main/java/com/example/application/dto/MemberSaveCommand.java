package com.example.application.dto;

public record MemberSaveCommand(
    String loginId,
    String password
) {

}

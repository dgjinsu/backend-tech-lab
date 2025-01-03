package com.example.cleanarchitecture.core.dto.command;

public record SaveMemberCommand(
    String loginId,
    String password
) {

}

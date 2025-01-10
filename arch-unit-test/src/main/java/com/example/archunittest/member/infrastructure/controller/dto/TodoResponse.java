package com.example.archunittest.member.infrastructure.controller.dto;

public record TodoResponse(
    int userId,
    int id,
    String title,
    boolean completed
) {

}

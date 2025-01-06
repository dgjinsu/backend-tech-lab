package com.example.archunittest.member.api.controller;

import com.example.archunittest.member.api.dto.MemberSaveRequest;
import com.example.archunittest.member.application.MemberUseCase;
import com.example.archunittest.member.application.dto.MemberSaveCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping("")
    public String test(@RequestBody MemberSaveRequest request) {
        memberUseCase.save(new MemberSaveCommand());
        return "test";
    }
}

package com.example.java17pinpoint.member.controller;

import com.example.java17pinpoint.member.entity.Member;
import com.example.java17pinpoint.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @PostMapping("")
    public String saveMember() {
        memberRepository.save(new Member("name"));
        return "success";
    }
}

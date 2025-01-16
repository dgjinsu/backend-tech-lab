package com.example.java17scouter.member.controller;

import com.example.java17scouter.member.entity.Member;
import com.example.java17scouter.member.repository.MemberRepository;
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

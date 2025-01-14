package com.example.springbootrabbitmq.member.controller;

import com.example.springbootrabbitmq.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/send-request")
    public String sendRequest(@RequestParam(name = "name") String name) {
        memberService.sendRequest(name);
        return "success";
    }
}

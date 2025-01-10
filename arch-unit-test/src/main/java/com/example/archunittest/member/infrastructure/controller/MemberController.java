package com.example.archunittest.member.infrastructure.controller;

import com.example.archunittest.member.infrastructure.controller.dto.MemberSaveRequest;
import com.example.archunittest.member.application.MemberUseCase;
import com.example.archunittest.member.application.dto.MemberSaveCommand;
import com.example.archunittest.member.infrastructure.controller.dto.TodoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping("")
    public String test(@RequestBody MemberSaveRequest request) {
        memberUseCase.save(new MemberSaveCommand());
        return "test";
    }

    @PostMapping("/api-call-test")
    public TodoResponse callApi(@RequestBody MemberSaveRequest request) {
        // 외부 API 호출
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://jsonplaceholder.typicode.com/todos/1";
        TodoResponse response = restTemplate.getForObject(apiUrl, TodoResponse.class);

        // 응답 출력
        System.out.println("Received Response: " + response);

        // 기존 로직
        memberUseCase.save(new MemberSaveCommand());

        return response;
    }
}

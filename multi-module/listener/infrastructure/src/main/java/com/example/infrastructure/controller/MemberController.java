package com.example.infrastructure.controller;

import com.example.application.dto.MemberSaveCommand;
import com.example.application.usecase.MemberUseCase;
import com.example.domain.member.Member;
import com.example.infrastructure.controller.dto.MemberSaveRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping("")
    public ResponseEntity<String> saveMember(@RequestBody MemberSaveRequest request) {
        memberUseCase.saveMember(new MemberSaveCommand(request.loginId(), request.password()));
        return ResponseEntity.ok("Member saved");
    }

    @GetMapping("")
    public ResponseEntity<List<Member>> getMembers() {
        List<Member> members = memberUseCase.getMembers();
        return ResponseEntity.ok(members);
    }
}

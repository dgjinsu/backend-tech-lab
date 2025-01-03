package com.example.cleanarchitecture.api.controller;

import com.example.cleanarchitecture.api.dto.request.SaveMemberRequest;
import com.example.cleanarchitecture.api.dto.response.MemberResponse;
import com.example.cleanarchitecture.core.application.MemberSaveUseCase;
import com.example.cleanarchitecture.core.dto.command.SaveMemberCommand;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberSaveUseCase memberSaveUseCase;

    @PostMapping("")
    public ResponseEntity<MemberResponse> saveMember(@RequestBody SaveMemberRequest request) {
        MemberQuery memberQuery = memberSaveUseCase.saveMember(
            new SaveMemberCommand(request.loginId(), request.password()));

        return ResponseEntity.ok(
            new MemberResponse(
                memberQuery.memberId(),
                memberQuery.loginId(),
                memberQuery.password())
        );
    }
}

package com.example.cleanarchitecture.api.controller;

import com.example.cleanarchitecture.api.dto.request.SaveMemberRequest;
import com.example.cleanarchitecture.api.dto.response.MemberListResponse;
import com.example.cleanarchitecture.api.dto.response.MemberResponse;
import com.example.cleanarchitecture.core.application.GetMemberUseCase;
import com.example.cleanarchitecture.core.application.SaveMemberUseCase;
import com.example.cleanarchitecture.core.dto.command.SaveMemberCommand;
import com.example.cleanarchitecture.core.dto.query.MemberListQuery;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;
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

    private final SaveMemberUseCase saveMemberUseCase;
    private final GetMemberUseCase getMemberUseCase;

    @PostMapping("")
    public ResponseEntity<MemberResponse> saveMember(@RequestBody SaveMemberRequest request) {
        MemberQuery memberQuery = saveMemberUseCase.saveMember(
            new SaveMemberCommand(request.loginId(), request.password()));

        return ResponseEntity.ok(
            new MemberResponse(
                memberQuery.memberId(),
                memberQuery.loginId(),
                memberQuery.password())
        );
    }

    @GetMapping("")
    public ResponseEntity<MemberListResponse> getAllMember() {
        MemberListQuery memberListQuery = getMemberUseCase.getAllMembers();

        return ResponseEntity.ok(
            new MemberListResponse(
                memberListQuery.memberQueryList().stream()
                    .map(query -> new MemberListResponse.MemberResponse(
                        query.memberId(),
                        query.loginId(),
                        query.password()
                    ))
                    .toList()
            )
        );
    }
}

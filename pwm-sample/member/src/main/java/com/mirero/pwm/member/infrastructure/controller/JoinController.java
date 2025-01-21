package com.mirero.pwm.member.infrastructure.controller;

import com.mirero.pwm.member.application.dto.command.SaveMemberCommand;
import com.mirero.pwm.member.application.dto.query.MemberQuery;
import com.mirero.pwm.member.application.usecase.SaveMemberUseCase;
import com.mirero.pwm.member.infrastructure.controller.dto.MemberResponse;
import com.mirero.pwm.member.infrastructure.controller.dto.SaveMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class JoinController {

    private final SaveMemberUseCase saveMemberUseCase;

    @PostMapping("")
    public ResponseEntity<MemberResponse> joinMember(@RequestBody SaveMemberRequest request) {
        MemberQuery memberQuery = saveMemberUseCase.saveMember(SaveMemberCommand.from(request));
        return ResponseEntity.ok(MemberResponse.from(memberQuery));
    }

}

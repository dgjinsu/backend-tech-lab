package com.mirero.pwm.member.infrastructure.controller;

import com.mirero.pwm.member.application.dto.command.SaveMemberCommand;
import com.mirero.pwm.member.application.dto.query.MemberQuery;
import com.mirero.pwm.member.application.usecase.SaveMemberUseCase;
import com.mirero.pwm.member.infrastructure.controller.dto.MemberResponse;
import com.mirero.pwm.member.infrastructure.controller.dto.SaveMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final SaveMemberUseCase saveMemberUseCase;

    @Operation(summary = "회원가입")
    @PostMapping("")
    public ResponseEntity<MemberResponse> joinMember(@RequestBody SaveMemberRequest request) {
        MemberQuery memberQuery = saveMemberUseCase.saveMember(SaveMemberCommand.from(request));
        return ResponseEntity.ok(MemberResponse.from(memberQuery));
    }

    @Operation(summary = "회원 정보 조회")
    @PostMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberInfo(@PathVariable Long memberId) {
        MemberQuery memberQuery = saveMemberUseCase.getMemberInfo(memberId);
        return ResponseEntity.ok(MemberResponse.from(memberQuery));
    }

}

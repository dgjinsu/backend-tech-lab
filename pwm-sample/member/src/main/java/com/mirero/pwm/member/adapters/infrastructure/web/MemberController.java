package com.mirero.pwm.member.adapters.infrastructure.web;

import com.mirero.pwm.member.application.usecase.member.commands.SaveMemberCommand;
import com.mirero.pwm.member.application.usecase.member.commands.dto.SaveMemberDto;
import com.mirero.pwm.member.application.usecase.member.queries.GetMemberQuery;
import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;
import com.mirero.pwm.member.adapters.infrastructure.web.dto.MemberResponse;
import com.mirero.pwm.member.adapters.infrastructure.web.dto.SaveMemberRequest;
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

    private final SaveMemberCommand saveMemberCommand;
    private final GetMemberQuery getMemberQuery;

    @Operation(summary = "회원가입")
    @PostMapping("")
    public ResponseEntity<MemberResponse> joinMember(@RequestBody SaveMemberRequest request) {
        GetMemberDto getMemberDto = saveMemberCommand.saveMember(SaveMemberDto.from(request));
        return ResponseEntity.ok(MemberResponse.from(getMemberDto));
    }

    @Operation(summary = "회원 정보 조회")
    @PostMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberInfo(@PathVariable Long memberId) {
        GetMemberDto getMemberDto = getMemberQuery.getMemberInfo(memberId);
        return ResponseEntity.ok(MemberResponse.from(getMemberDto));
    }
}

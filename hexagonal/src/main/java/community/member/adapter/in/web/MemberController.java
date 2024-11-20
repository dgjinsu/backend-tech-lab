package community.member.adapter.in.web;

import community.global.dto.Response;
import community.member.adapter.in.dto.JoinRequest;
import community.member.adapter.in.dto.MemberResponse;
import community.member.adapter.in.dto.MemberUpdateRequest;
import community.member.adapter.in.dto.MemberUpdateResponse;
import community.member.application.port.in.JoinUseCase;
import community.member.application.port.in.MemberListQuery;
import community.member.application.port.in.MemberUpdateUseCase;
import community.member.application.port.in.command.JoinCommand;
import community.member.application.port.in.command.MemberUpdateCommand;
import community.member.application.port.in.result.MemberUpdateResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final JoinUseCase joinUseCase;
    private final MemberListQuery memberListUseCase;
    private final MemberUpdateUseCase memberUpdateUseCase;

    @PostMapping("")
    public ResponseEntity<Response<Long>> joinMember(@RequestBody JoinRequest request) {
        JoinCommand joinCommand = new JoinCommand(request.getLoginId(), request.getPassword(), request.getPassword());
        return ResponseEntity.ok(new Response<>(joinUseCase.joinMember(joinCommand), "회원가입 완료"));
    }

    @GetMapping("")
    public ResponseEntity<Response<List<MemberResponse>>> getMemberList() {
        List<MemberResponse> memberResponseList = memberListUseCase.getMemberList().stream()
            .map(MemberResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(memberResponseList, "전체 회원 조회 완료"));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<Response<MemberUpdateResponse>> updateMember(@PathVariable("memberId") Long memberId,
        @RequestBody MemberUpdateRequest request) {
        MemberUpdateCommand command = new MemberUpdateCommand(memberId, request.getPassword(), request.getName());
        MemberUpdateResult result = memberUpdateUseCase.updateMember(command);
        MemberUpdateResponse response = new MemberUpdateResponse(result.getMemberId(), result.getPassword(),
            result.getName());
        return ResponseEntity.ok(new Response<>(response, "회원 정보 수정 완료"));
    }
}

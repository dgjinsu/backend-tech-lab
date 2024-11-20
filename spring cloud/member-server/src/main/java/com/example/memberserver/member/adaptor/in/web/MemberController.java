package com.example.memberserver.member.adaptor.in.web;

import com.example.common.security.PrincipalDetails;
import com.example.memberserver.member.adaptor.in.web.dto.LoginRequest;
import com.example.memberserver.member.adaptor.in.web.dto.MemberInfoResponse;
import com.example.memberserver.member.adaptor.in.web.dto.MemberSaveRequest;
import com.example.memberserver.member.application.port.in.GetMemberInfoQuery;
import com.example.memberserver.member.application.port.in.LoginQuery;
import com.example.memberserver.member.application.port.in.SaveMemberUseCase;
import com.example.memberserver.member.infrastructure.common.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member-server/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final GetMemberInfoQuery getMemberInfoQuery;
    private final LoginQuery loginQuery;
    private final SaveMemberUseCase saveMemberUseCase;

    @PostMapping("")
    public ResponseEntity<Response> saveTemp(@RequestBody MemberSaveRequest request) {
        return ResponseEntity.ok(new Response(saveMemberUseCase.saveMember(request), "Member 저장 완료"));
    }

    @PostMapping("login")
    public ResponseEntity<Response> saveTemp(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new Response(loginQuery.login(request), "로그인 완료"));
    }

    @GetMapping("/info")
    public MemberInfoResponse getInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletRequest request) {
        Map<String, String> headers = getHeadersInfo(request);
        headers.forEach((key, value) -> System.out.println(key + ": " + value));
        return getMemberInfoQuery.getMemberInfo(principalDetails.getMember().getId());
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }
}

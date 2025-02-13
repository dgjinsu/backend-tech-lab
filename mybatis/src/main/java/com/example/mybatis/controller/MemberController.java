package com.example.mybatis.controller;

import com.example.mybatis.dto.MemberResponse;
import com.example.mybatis.dto.SaveMemberRequest;
import com.example.mybatis.repository.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberMapper memberMapper;

    @PostMapping("")
    public ResponseEntity<String> saveMember(@RequestBody SaveMemberRequest request) {
        memberMapper.saveMember(request);
        return ResponseEntity.ok("회원 저장 완료");
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberId) {
        MemberResponse response = memberMapper.findById(memberId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}

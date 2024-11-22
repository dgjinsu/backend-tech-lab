package com.example.transaction.controller;

import com.example.transaction.entity.Member;
import com.example.transaction.service.AService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final AService aService;

    @GetMapping("/api/member/mandatory")
    public ResponseEntity<Void> saveWithMandatorySuccess() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);
        aService.saveWithMandatorySuccess(aMember, bMember);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/member/mandatory-fail")
    public ResponseEntity<Void> saveWithMandatoryFail() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);
        aService.saveWithMandatoryFail(aMember, bMember);
        return ResponseEntity.ok().build();
    }
}

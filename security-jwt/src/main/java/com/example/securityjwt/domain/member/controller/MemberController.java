package com.example.securityjwt.domain.member.controller;

import com.example.securityjwt.domain.member.entity.Member;
import com.example.securityjwt.domain.member.entity.Role;
import com.example.securityjwt.domain.member.repository.MemberRepository;
import com.example.securityjwt.global.security.JwtProvier;
import com.example.securityjwt.global.security.annotaion.RequiredAdminRole;
import com.example.securityjwt.global.security.annotaion.RequiredLogin;
import com.example.securityjwt.global.security.annotaion.RequiredUserRole;
import com.example.securityjwt.global.security.dto.LoginMember;
import com.example.securityjwt.global.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JwtProvier jwtProvier;

    @PostMapping("/join")
    public ResponseEntity<?> joinMember(@RequestBody JoinRequest request) {
        memberRepository.save(
            new Member(
                request.loginId,
                encoder.encode(request.password()),
                Role.USER
            )
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> joinMember(@RequestBody LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.loginId()).get();
        String accessToken = jwtProvier.createAccessToken(member.getId(), member.getRole());
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/user-role-test")
    @RequiredUserRole
    public void userRoleTest(LoginMember loginMember) {
        log.info("loginMemberId: {}", loginMember.getMember().memberId());
        log.info("loginMemberId: {}", SecurityUtils.getLoginMember().getMember().memberId());
    }

    @GetMapping("/admin-role-test")
    @RequiredAdminRole
    public void adminRoleTest(LoginMember loginMember) {
        log.info("loginMemberId: {}", loginMember.getMember().memberId());
    }

    @GetMapping("/authentication-test")
    @RequiredLogin
    public void authenticationTest(LoginMember loginMember) {
        log.info("loginMemberId: {}", loginMember.getMember().memberId());
    }

    private static record JoinRequest(
        String loginId,
        String password,
        Role role
    ) {

    }

    private static record LoginRequest(
        String loginId,
        String password
    ) {

    }
}

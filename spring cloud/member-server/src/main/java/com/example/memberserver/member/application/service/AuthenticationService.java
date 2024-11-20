package com.example.memberserver.member.application.service;

import com.example.memberserver.member.adaptor.in.web.dto.LoginRequest;
import com.example.memberserver.member.adaptor.in.web.dto.LoginResponse;
import com.example.memberserver.member.application.port.in.LoginQuery;
import com.example.memberserver.member.application.port.out.MemberInfoPort;
import com.example.memberserver.member.domain.Member;
import com.example.memberserver.member.infrastructure.exception.ErrorCode;
import com.example.memberserver.member.infrastructure.exception.MemberException;
import com.example.memberserver.member.infrastructure.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements LoginQuery {

    private final MemberInfoPort memberInfoPort;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        // id 체크
        Member member = memberInfoPort.findByLoginId(request.getLoginId());

        // authCode 체크
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // accessToken & refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole());

        return new LoginResponse(accessToken);
    }
}

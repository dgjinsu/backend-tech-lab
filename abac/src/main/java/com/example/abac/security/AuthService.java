package com.example.abac.security;

import com.example.abac.security.dto.LoginRequest;
import com.example.abac.security.dto.LoginResponse;
import com.example.abac.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public LoginResponse login(LoginRequest request) {
        // [학습 포인트] '인증 이전' 토큰: username/password만 담긴 '미인증' 상태의 Authentication.
        // AuthenticationManager가 내부적으로 CustomUserDetailsService.loadUserByUsername()을 호출하고
        // BCrypt 비교까지 끝내면 '인증 완료' Authentication을 반환. 실패 시 AuthenticationException.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        // getPrincipal()은 UserDetails. 우리는 CustomUserPrincipal을 리턴하므로 안전 캐스팅.
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        // Principal의 ABAC 속성(userId, role, departmentId)을 JWT claim에 봉인.
        String token = tokenProvider.generate(principal);
        return new LoginResponse(token, principal.getUsername(), principal.getRole(), principal.getDepartmentId());
    }
}

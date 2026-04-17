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
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        String token = tokenProvider.generate(principal);
        return new LoginResponse(token, principal.getUsername(), principal.getRole(), principal.getDepartmentId());
    }
}

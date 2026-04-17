package com.example.abac.security.jwt;

import com.example.abac.security.CustomUserPrincipal;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// [학습 포인트] JWT → SecurityContext 변환 지점. 이 필터가 없으면
// @PreAuthorize가 authentication을 null로 보고 전부 거부해버린다.
// OncePerRequestFilter: 포워딩/디스패치 등으로 같은 요청을 여러 번 타도 필터가 한 번만 돌도록 보장.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);
        // 토큰이 없거나, 이미 앞선 필터가 인증을 끝냈다면 건너뜀(덮어쓰기 방지).
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // [핵심 변환] JWT → Principal. parse 시점에 서명/만료가 검증되고 claim이 복원된다.
                CustomUserPrincipal principal = tokenProvider.parse(token);
                // credentials(두 번째 인자)는 null. JWT 검증이 이미 끝났으니 비밀번호를 들고 다닐 이유가 없다.
                // authorities를 넘기는 세 번째 인자 생성자를 쓰면 '인증 완료' 상태로 표시됨(isAuthenticated=true).
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()
                );
                // remote IP, session id 등 요청 메타를 Authentication에 부착(감사 로그에 유용).
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // [핵심] 여기서 SecurityContext가 채워진다. 이후 체인(컨트롤러, @PreAuthorize)은
                // SecurityContextHolder.getContext().getAuthentication()으로 이 값을 읽는다.
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException ignored) {
                // 위·변조, 만료, 포맷 이상 등. 401로 내려주는 건 SecurityConfig의 EntryPoint가 담당.
                // 여기서는 Context만 깨끗이 비우고 다음 필터로 넘긴다.
                SecurityContextHolder.clearContext();
            }
        }
        // Context가 비어 있으면 authorizeHttpRequests의 anyRequest().authenticated()가 401을 찍는다.
        filterChain.doFilter(request, response);
    }

    // "Authorization: Bearer <token>" 헤더에서 토큰만 떼어낸다. 접두사 없으면 null 반환.
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

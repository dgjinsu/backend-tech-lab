package com.example.common.security;


import com.example.common.role.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DelegationTokenExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        try {
            // X-User-Id와 X-User-Roles 헤더 확인
            String userIdHeader = request.getHeader("X-User-Id");
            String userRolesHeader = request.getHeader("X-User-Roles");

            if (userIdHeader != null && userRolesHeader != null) {
                // X-User-Id와 X-User-Roles 헤더가 있는 경우 이를 사용해 PrincipalDetails 생성
                Long memberId = Long.valueOf(userIdHeader);
                Role role = Role.valueOf(userRolesHeader);

                PrincipalDetails principalDetails = new PrincipalDetails(
                    new MemberDto(memberId, null, role));

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, null,
                    principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("SecurityContext에 X-User-Id: {} 와 X-User-Roles: {} 헤더 값으로 인증 정보가 설정되었습니다.",
                    memberId, role);
            } else {
                log.warn("X-User-Id 또는 X-User-Roles 헤더가 요청에 없습니다.");
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }
    }
}

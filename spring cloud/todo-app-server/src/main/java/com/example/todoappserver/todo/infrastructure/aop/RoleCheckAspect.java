package com.example.todoappserver.todo.infrastructure.aop;

import com.example.todoappserver.common.annotation.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final HttpServletRequest request;

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        // 헤더에서 역할 정보 가져오기
        String userRoles = request.getHeader("X-User-Roles");
        if (userRoles == null || !userRoles.contains(requireRole.value())) {
            log.warn("접근 권한 없음: 필요한 역할={}, 사용자 역할={}", requireRole.value(), userRoles);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        // 역할이 일치하면 메서드 실행
        return joinPoint.proceed();
    }
}

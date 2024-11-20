package com.example.todoappserver.todo.infrastructure.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // X-User-Id 헤더 추가
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                requestTemplate.header("X-User-Id", userId);
            }

            // X-User-Roles 헤더 추가
            String roles = request.getHeader("X-User-Roles");
            if (roles != null) {
                requestTemplate.header("X-User-Roles", roles);
            }
        }
    }
}
package com.example.common.annotation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import org.springframework.stereotype.Component;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        log.info("resolveArgument 진입");
        if (request != null) {
            String memberId = request.getHeader("X-User-Id"); // "X-User-Id" 헤더에서 값 가져오기

            if (memberId != null) {
                return Long.valueOf(memberId); // 헤더 값을 String 타입으로 반환
            } else {
                log.warn("X-User-Id 헤더가 요청에 없습니다.");
            }
        }
        return null; // 헤더에 값이 없을 경우 null 반환
    }
}
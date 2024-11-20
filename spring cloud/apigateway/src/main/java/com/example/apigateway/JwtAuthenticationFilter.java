package com.example.apigateway;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DelegationTokenExpiredException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtTokenProvider jwtProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                // 만료 체크
                if (jwtProvider.isExpiration(token)) {
                    log.info("access token 만료");
                    throw new DelegationTokenExpiredException("토큰 만료");
                }

                // 토큰 검증
                String memberId = jwtProvider.getLoginId(token);
                String role = jwtProvider.getRoles(token);

//                // 권한 체크
//                if (config.getRequiredRole() != null && !role.contains(config.getRequiredRole())) {
//                    log.info("권한 부족: 필요한 역할 = {}, 사용자의 역할 = {}", config.getRequiredRole(), role);
//                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 리소스에 접근 권한이 없습니다.");
//                }

                // 사용자 정보를 헤더에 추가
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", memberId)
                        .header("X-User-Roles", role)
                        .build();

                // 새 요청을 포함한 Exchange 생성
                exchange = exchange.mutate().request(modifiedRequest).build();
            }
            return chain.filter(exchange);
        };
    }

//    @Data
    public static class Config {
//         필터 구성에 필요한 설정 정보를 추가
//        private String requiredRole;
    }
}
package com.example.todoappserver.todo.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Resilience4jConfig {

    private final RetryRegistry retryRegistry;

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(
                "customCircuitBreaker",
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50) // 실패율 기준
                        .slowCallRateThreshold(50) // 느린 호출 실패율
                        .slowCallDurationThreshold(Duration.ofSeconds(3)) // 느린 호출 판단 기준 시간
                        .permittedNumberOfCallsInHalfOpenState(3) // Half-Open 상태에서 허용되는 호출 횟수
                        .maxWaitDurationInHalfOpenState(Duration.ofSeconds(3)) // Half-Open 상태에서 최대 대기 시간
                        .slidingWindowType(COUNT_BASED) // 호출 횟수 기준으로 판단
                        .slidingWindowSize(10) // 최근 10번 호출 기준으로 실패율 계산
                        .minimumNumberOfCalls(5) // 최소 호출 횟수
                        .waitDurationInOpenState(Duration.ofSeconds(60)) // Open 상태에서 대기 시간
                        .build()
        );

        return circuitBreaker;
    }

    @Bean
    public Retry retry() {
        return retryRegistry.retry("customRetry",
                RetryConfig.custom()
                        .maxAttempts(3) // 최대 재시도 횟수
                        .waitDuration(Duration.ofMillis(500)) // 재시도 간 대기 시간
                        .build()
        );
    }
}
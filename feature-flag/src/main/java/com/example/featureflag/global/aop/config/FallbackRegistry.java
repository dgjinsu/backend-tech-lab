package com.example.featureflag.global.aop.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @FeatureFallback 어노테이션이 붙은 메서드를 등록하고 관리하는 레지스트리
 * <p>
 * 하나의 key에는 하나의 fallback만 허용됩니다.
 * 중복 등록 시 IllegalStateException이 발생합니다.
 * </p>
 */
@Component
@Slf4j
public class FallbackRegistry {

    // Map<featureKey, FallbackMethodInfo>
    // 하나의 key에 하나의 fallback만 저장
    private final Map<String, FallbackMethodInfo> fallbackRegistry = new ConcurrentHashMap<>();

    /**
     * Fallback 메서드를 등록합니다.
     *
     * @param key    feature flag key
     * @param method fallback 메서드
     * @param bean   메서드가 속한 Spring Bean 인스턴스
     * @throws IllegalStateException 같은 key에 이미 fallback이 등록되어 있는 경우
     */
    public void registerFallback(String key, Method method, Object bean) {
        // 중복 key 검증 - fail-fast
        FallbackMethodInfo existing = fallbackRegistry.get(key);
        if (existing != null) {
            throw new IllegalStateException("기본 메서드가 없어 Fallback 메서드를 등록시킬 수 없습니다.");
        }

        FallbackMethodInfo info = new FallbackMethodInfo(method, bean);
        fallbackRegistry.put(key, info);

        log.info("Registered fallback: key={}, method={}, bean={}",
                key, method.getName(), bean.getClass().getSimpleName());
    }

    /**
     * 주어진 key에 대한 fallback을 반환합니다.
     *
     * @param key feature flag key
     * @return 등록된 fallback (없으면 Optional.empty())
     */
    public Optional<FallbackMethodInfo> getFallback(String key) {
        return Optional.ofNullable(fallbackRegistry.get(key));
    }

    /**
     * Fallback 메서드 정보를 담는 Value Object
     */
    @Getter
    @AllArgsConstructor
    public static class FallbackMethodInfo {
        private final Method method;
        private final Object bean;

        /**
         * Fallback 메서드를 실행합니다.
         *
         * @param args 메서드 인자
         * @return 실행 결과
         * @throws Exception 실행 중 발생한 예외
         */
        public Object invoke(Object[] args) throws Exception {
            method.setAccessible(true);
            return method.invoke(bean, args);
        }
    }
}

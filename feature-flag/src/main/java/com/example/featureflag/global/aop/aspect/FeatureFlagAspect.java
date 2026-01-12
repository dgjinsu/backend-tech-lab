package com.example.featureflag.global.aop.aspect;

import com.example.featureflag.global.aop.annotation.FeatureFlag;
import com.example.featureflag.global.aop.config.FallbackRegistry;
import com.example.featureflag.global.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureFlagAspect {

    private final FeatureFlagService featureFlagService;
    private final FallbackRegistry fallbackRegistry;

    @Around("@annotation(com.example.featureflag.global.aop.annotation.FeatureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FeatureFlag featureFlag = method.getAnnotation(FeatureFlag.class);

        String featureKey = featureFlag.key();
        boolean isEnabled = featureFlagService.isEnabled(featureKey);

        log.info("Feature flag check: {} = {}", featureKey, isEnabled);

        if (isEnabled) {
            // Feature가 활성화되어 있으면 원래 메서드 실행
            return joinPoint.proceed();
        } else {
            // Feature가 비활성화되어 있으면 fallback 처리
            return handleDisabledFeature(joinPoint, featureFlag, featureKey);
        }
    }

    /**
     * Feature가 비활성화되었을 때 fallback 우선순위에 따라 처리
     * 1. @FeatureFallback 어노테이션 (cross-bean) - 최우선
     * 2. fallbackMethod 속성 (same-class) - 하위 호환성
     * 3. 예외 발생 - fallback 없음
     */
    private Object handleDisabledFeature(ProceedingJoinPoint joinPoint,
                                         FeatureFlag featureFlag,
                                         String featureKey) throws Throwable {
        // Strategy 1: @FeatureFallback 어노테이션으로 등록된 fallback 찾기 (cross-bean)
        Optional<FallbackRegistry.FallbackMethodInfo> registeredFallback =
                fallbackRegistry.getFallback(featureKey);

        if (registeredFallback.isPresent()) {
            return invokeRegisteredFallback(joinPoint, registeredFallback.get(), featureKey);
        }

        // Strategy 2: fallbackMethod 속성으로 지정된 같은 클래스 내 메서드 찾기 (same-class)
        String fallbackMethodName = featureFlag.fallbackMethod();
        if (!fallbackMethodName.isEmpty()) {
            return invokeSameClassFallback(joinPoint, fallbackMethodName);
        }

        // Strategy 3: Fallback이 없으면 예외 발생
        log.warn("Feature '{}' is disabled and no fallback method defined", featureKey);
        throw new IllegalStateException(
                String.format("Feature '%s' is currently disabled", featureKey)
        );
    }

    /**
     * @FeatureFallback 어노테이션으로 등록된 fallback 메서드 실행
     */
    private Object invokeRegisteredFallback(ProceedingJoinPoint joinPoint,
                                            FallbackRegistry.FallbackMethodInfo fallbackInfo,
                                            String featureKey) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            // 파라미터 타입 검증
            Method fallbackMethod = fallbackInfo.getMethod();
            Class<?>[] fallbackParams = fallbackMethod.getParameterTypes();
            Class<?>[] originalParams = signature.getParameterTypes();

            if (!Arrays.equals(fallbackParams, originalParams)) {
                log.error("Parameter type mismatch for fallback: key={}, expected={}, actual={}",
                        featureKey, Arrays.toString(originalParams), Arrays.toString(fallbackParams));
                throw new IllegalStateException(
                        String.format("Fallback method parameter types do not match for key '%s'", featureKey)
                );
            }

            log.info("Invoking registered fallback: key={}, method={}, bean={}",
                    featureKey, fallbackMethod.getName(),
                    fallbackInfo.getBean().getClass().getSimpleName());

            return fallbackInfo.invoke(args);

        } catch (InvocationTargetException e) {
            log.error("Error invoking registered fallback for key: {}", featureKey, e.getCause());
            throw e.getCause();
        } catch (Exception e) {
            log.error("Unexpected error invoking registered fallback for key: {}", featureKey, e);
            throw new IllegalStateException("Failed to invoke registered fallback: " + featureKey, e);
        }
    }

    /**
     * 같은 클래스 내의 fallback 메서드 실행 (하위 호환성)
     */
    private Object invokeSameClassFallback(ProceedingJoinPoint joinPoint, String fallbackMethodName) throws Throwable {
        try {
            Object target = joinPoint.getTarget();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method fallbackMethod = target.getClass()
                    .getDeclaredMethod(fallbackMethodName, signature.getParameterTypes());

            fallbackMethod.setAccessible(true);
            log.info("Invoking same-class fallback: method={}", fallbackMethodName);
            return fallbackMethod.invoke(target, joinPoint.getArgs());
        } catch (NoSuchMethodException e) {
            log.error("Fallback method '{}' not found", fallbackMethodName, e);
            throw new IllegalStateException("Fallback method not found: " + fallbackMethodName, e);
        } catch (InvocationTargetException e) {
            log.error("Error invoking same-class fallback method '{}'", fallbackMethodName, e.getCause());
            throw e.getCause();
        }
    }
}

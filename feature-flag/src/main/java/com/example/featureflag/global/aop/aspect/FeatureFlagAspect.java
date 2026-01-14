package com.example.featureflag.global.aop.aspect;

import com.example.featureflag.global.aop.annotation.FeatureFlag;
import com.example.featureflag.global.aop.config.FallbackRegistry;
import com.example.featureflag.global.service.FeatureFlagService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureFlagAspect {

    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final FeatureFlagService featureFlagService;
    private final FallbackRegistry fallbackRegistry;

    @Around("@annotation(com.example.featureflag.global.aop.annotation.FeatureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FeatureFlag featureFlag = method.getAnnotation(FeatureFlag.class);

        String featureKey = featureFlag.key();

        // 현재 사용자 Role 가져오기
        Optional<String> currentRole = getCurrentUserRole();

        // Role 기반 feature flag 적용 여부 확인
        boolean shouldApplyFeatureFlag = shouldApplyFeatureFlag(featureKey, currentRole);

        log.info("Feature Flag 체크: key={}, role={}, 적용여부={}",
                featureKey, currentRole.orElse("N/A"), shouldApplyFeatureFlag);

        if (!shouldApplyFeatureFlag) {
            log.info("Role {}은 Feature Flag {} 적용 대상이 아님. 원래 메서드 실행",
                    currentRole.orElse("N/A"), featureKey);
            return joinPoint.proceed();
        }

        // Feature Flag 적용 대상인 경우 기존 로직 수행
        boolean isEnabled = featureFlagService.isEnabled(featureKey);

        if (isEnabled) {
            // Feature가 활성화되어 있으면 원래 메서드 실행
            return joinPoint.proceed();
        } else {
            // Feature가 비활성화되어 있으면 fallback 처리
            return handleDisabledFeature(joinPoint, featureFlag, featureKey);
        }
    }

    /**
     * 현재 HTTP 요청에서 X-User-Role 헤더 값을 가져옴
     */
    private Optional<String> getCurrentUserRole() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();
        String role = request.getHeader(USER_ROLE_HEADER);

        return Optional.ofNullable(role);
    }

    /**
     * Feature Flag를 적용해야 하는지 결정
     * - Role 헤더가 없으면 feature flag 적용하지 않음 (원래 메서드 실행)
     * - Role이 적용 대상이면 feature flag 로직 적용
     */
    private boolean shouldApplyFeatureFlag(String featureKey, Optional<String> currentRole) {
        if (currentRole.isEmpty()) {
            return false;
        }
        return featureFlagService.isTargetRole(featureKey, currentRole.get());
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
        log.warn("Feature '{}'가 비활성화되어 있고 fallback 메서드가 정의되지 않음", featureKey);
        throw new IllegalStateException(
                String.format("Feature '%s'가 현재 비활성화 상태입니다", featureKey)
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
                log.error("Fallback 파라미터 타입 불일치: key={}, 예상={}, 실제={}",
                        featureKey, Arrays.toString(originalParams), Arrays.toString(fallbackParams));
                throw new IllegalStateException(
                        String.format("key '%s'의 Fallback 메서드 파라미터 타입이 일치하지 않습니다", featureKey)
                );
            }

            log.info("등록된 Fallback 실행: key={}, method={}, bean={}",
                    featureKey, fallbackMethod.getName(),
                    fallbackInfo.getBean().getClass().getSimpleName());

            return fallbackInfo.invoke(args);

        } catch (InvocationTargetException e) {
            log.error("등록된 Fallback 실행 중 오류 발생: key={}", featureKey, e.getCause());
            throw e.getCause();
        } catch (Exception e) {
            log.error("등록된 Fallback 실행 중 예상치 못한 오류 발생: key={}", featureKey, e);
            throw new IllegalStateException("등록된 Fallback 실행 실패: " + featureKey, e);
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
            log.info("같은 클래스 내 Fallback 실행: method={}", fallbackMethodName);
            return fallbackMethod.invoke(target, joinPoint.getArgs());
        } catch (NoSuchMethodException e) {
            log.error("Fallback 메서드 '{}' 찾을 수 없음", fallbackMethodName, e);
            throw new IllegalStateException("Fallback 메서드를 찾을 수 없습니다: " + fallbackMethodName, e);
        } catch (InvocationTargetException e) {
            log.error("같은 클래스 내 Fallback 메서드 '{}' 실행 중 오류 발생", fallbackMethodName, e.getCause());
            throw e.getCause();
        }
    }
}

package com.example.featureflag.config.aspect;

import com.example.featureflag.config.annotation.FeatureFlag;
import com.example.featureflag.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class FeatureFlagAspect {
    
    private final FeatureFlagService featureFlagService;
    
    @Around("@annotation(featureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint joinPoint, FeatureFlag featureFlag) throws Throwable {
        String featureName = featureFlag.value();
        boolean isEnabled = featureFlagService.isFeatureEnabled(featureName);
        
        log.info("피쳐 플래그 체크: {} = {}", featureName, isEnabled);
        
                if (isEnabled) {
            // 피쳐가 활성화되어 있으면 원래 메서드 실행
            log.debug("피쳐 '{}' 활성화됨 - 메서드 실행", featureName);
            return joinPoint.proceed();
        } else {
            // 피쳐가 비활성화되어 있음 - 항상 예외 발생
            log.warn("피쳐 '{}' 비활성화됨 - 예외 발생", featureName);
            throw new FeatureNotEnabledException("피쳐 플래그 '" + featureName + "'가 비활성화되어 있습니다.");
        }
    }
    

    
    /**
     * 피쳐 플래그가 비활성화일 때 발생하는 예외
     */
    public static class FeatureNotEnabledException extends RuntimeException {
        public FeatureNotEnabledException(String message) {
            super(message);
        }
    }
}

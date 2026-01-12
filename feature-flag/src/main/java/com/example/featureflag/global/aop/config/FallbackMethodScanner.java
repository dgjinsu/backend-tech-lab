package com.example.featureflag.global.aop.config;

import com.example.featureflag.global.aop.annotation.FeatureFallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Spring Bean 생성 시 @FeatureFallback 어노테이션이 붙은 메서드를 스캔하여
 * FallbackRegistry에 자동으로 등록하는 BeanPostProcessor
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FallbackMethodScanner implements BeanPostProcessor {

    private final FallbackRegistry fallbackRegistry;

    /**
     * Bean 초기화 후 @FeatureFallback 어노테이션 스캔 및 등록
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // AOP 프록시가 있는 경우 실제 타겟 클래스를 가져옴
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        // 클래스의 모든 메서드 스캔 (상속된 메서드 포함)
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(targetClass);

        for (Method method : methods) {
            // @FeatureFallback 어노테이션 찾기
            FeatureFallback annotation = AnnotationUtils.findAnnotation(method, FeatureFallback.class);

            if (annotation != null) {
                String key = annotation.key();

                // FallbackRegistry에 등록
                fallbackRegistry.registerFallback(key, method, bean);

                log.debug("Found @FeatureFallback: bean={}, method={}, key={}",
                        beanName, method.getName(), key);
            }
        }

        return bean;
    }
}

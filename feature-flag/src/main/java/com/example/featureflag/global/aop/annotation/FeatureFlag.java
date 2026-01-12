package com.example.featureflag.global.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureFlag {

    String key();

    /**
     * 기능이 비활성화되었을 때 실행할 fallback 메서드명 (선택사항)
     */
    String fallbackMethod() default "";
}

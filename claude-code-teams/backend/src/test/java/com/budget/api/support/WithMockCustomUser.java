package com.budget.api.support;

import com.budget.api.domain.user.entity.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long userId() default 1L;

    Role role() default Role.EMPLOYEE;

    long departmentId() default 1L;
}

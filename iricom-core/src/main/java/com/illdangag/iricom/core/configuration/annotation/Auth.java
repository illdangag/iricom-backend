package com.illdangag.iricom.core.configuration.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Controller에서 권한을 설정
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Auth {
    AuthRole[] role() default AuthRole.NONE;
}

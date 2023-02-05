package com.illdangag.iricom.server.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auth 어노테이션이 설정 되어 있는 Controller에서 계정 정보 전달
 * client가 인증을 사용하지 않으면 null이 반환
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestContext {
}

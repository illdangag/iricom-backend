package com.illdangag.iricom.core.configuration.aspect;

import com.illdangag.iricom.core.configuration.annotation.ApiCallLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * API 요청 로그
 */
@Slf4j
@Component
@Aspect
public class ApiCallLogAspect {
    @Around("within(com.illdangag.iricom.core.controller..*)")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String simpleClassName = methodSignature.getMethod().getDeclaringClass().getSimpleName();
        String methodName = methodSignature.getName();

        ApiCallLog apiCallLog = methodSignature.getMethod().getAnnotation(ApiCallLog.class);
        if (apiCallLog != null) {
            String apiCode = apiCallLog.apiCode();
            if (!apiCode.isEmpty()) {
                log.info("[REST_API][{}][{}.{}]", apiCode, simpleClassName, methodName);
            }
        }

        return proceedingJoinPoint.proceed();
    }
}

package com.illdangag.iricom.server.configuration.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 특정 메서드의 실행 시간 측정
 */
@Slf4j
@Component
@Aspect
public class ElapseLoggableAspect {
    @Around("@annotation(com.illdangag.iricom.server.configuration.annotation.ElapseLoggable)")
    public Object elapseTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String simpleClassName = methodSignature.getMethod().getDeclaringClass().getSimpleName();
        String methodName = methodSignature.getName();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            result = proceedingJoinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("[EXECUTE_TIME][{}.{}]: {}ms", simpleClassName, methodName, stopWatch.getLastTaskTimeMillis());
        }

        return result;
    }
}

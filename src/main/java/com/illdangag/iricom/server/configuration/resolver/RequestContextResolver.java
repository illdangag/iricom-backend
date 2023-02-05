package com.illdangag.iricom.server.configuration.resolver;

import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 요청한 계정의 정보를 Controller로 전달
 */
@Slf4j
@Component
public class RequestContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        RequestContext requestContext = methodParameter.getParameter().getAnnotation(RequestContext.class);
        if (requestContext == null) {
            return false;
        }

        Class<?> targetClass = methodParameter.getParameterType();
        return targetClass.equals(Account.class)
                || targetClass.equals(Board[].class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Class targetClass = methodParameter.getParameterType();
        if (targetClass.equals(Account.class)) {
            return nativeWebRequest.getAttribute("account", 0);
        } else if (targetClass.equals(Board[].class)) {
            return nativeWebRequest.getAttribute("boards", 0);
        }
        return null;
    }
}

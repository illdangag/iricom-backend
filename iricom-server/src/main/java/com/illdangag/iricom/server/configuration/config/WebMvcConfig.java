package com.illdangag.iricom.server.configuration.config;

import com.illdangag.iricom.server.configuration.resolver.RequestContextResolver;
import com.illdangag.iricom.server.data.request.PersonalMessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Qualifier("WebMvcConfig")
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final HandlerInterceptor firebaseAuthInterceptor;
    private final RequestContextResolver requestContextResolver;

    @Autowired
    public WebMvcConfig(HandlerInterceptor firebaseAuthInterceptor, RequestContextResolver requestContextResolver) {
        this.firebaseAuthInterceptor = firebaseAuthInterceptor;
        this.requestContextResolver = requestContextResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.firebaseAuthInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this.requestContextResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PersonalMessageStatus.PersonalMessageStatusConverter());
    }
}

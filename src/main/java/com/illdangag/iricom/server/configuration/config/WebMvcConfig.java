package com.illdangag.iricom.server.configuration.config;

import com.illdangag.iricom.server.configuration.interceptor.FirebaseAuthInterceptor;
import com.illdangag.iricom.server.configuration.resolver.RequestContextResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final FirebaseAuthInterceptor firebaseAuthInterceptor;
    private final RequestContextResolver requestContextResolver;

    @Autowired
    public WebMvcConfig(FirebaseAuthInterceptor firebaseAuthInterceptor, RequestContextResolver requestContextResolver) {
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
}

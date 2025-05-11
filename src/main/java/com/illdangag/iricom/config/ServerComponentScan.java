package com.illdangag.iricom.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.illdangag.iricom")
@EntityScan(basePackages = "com.illdangag.iricom")
public class ServerComponentScan {
}

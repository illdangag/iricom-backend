package com.illdangag.iricom.storage.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.illdangag.iricom.core", "com.illdangag.iricom.storage"})
@EntityScan(basePackages =  {"com.illdangag.iricom.core", "com.illdangag.iricom.storage"})
public class ComponentScanConfig {
}

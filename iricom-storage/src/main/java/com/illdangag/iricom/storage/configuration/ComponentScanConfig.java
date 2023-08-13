package com.illdangag.iricom.storage.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.illdangag.iricom.server", "com.illdangag.iricom.storage"})
public class ComponentScanConfig {
}

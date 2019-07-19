package com.registry.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot Config
 * @author LEE
 *
 */
@Configuration
@ComponentScan(basePackages = {"com.registry"})
public class ApplicationConfig {
}

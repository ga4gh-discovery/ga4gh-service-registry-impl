package com.dnastack.discovery.registry.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@EntityScan(basePackages = "com.dnastack.discovery.registry.domain")
@Configuration
public class BaseConfig {

}

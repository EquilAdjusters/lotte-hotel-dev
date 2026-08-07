package com.example.backendlotte.notification.solapi;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
    SolapiProperties.class
)
public class SolapiConfig {
}
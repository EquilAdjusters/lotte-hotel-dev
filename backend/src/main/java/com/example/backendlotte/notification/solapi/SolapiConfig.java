package com.example.backendlotte.notification.solapi;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SolapiConfig {

    private final SolapiProperties properties;

    @PostConstruct
    public void check() {
        log.info(
            "SOLAPI config loaded - apiKey={}, apiSecret={}, sender={}",
            properties.apiKey() != null && !properties.apiKey().isBlank(),
            properties.apiSecret() != null && !properties.apiSecret().isBlank(),
            properties.sender() != null && !properties.sender().isBlank()
        );
    }
}
package com.example.backendlotte.notification.solapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "app.notification.solapi"
)
public record SolapiProperties(
    String apiKey,
    String apiSecret,
    String fromNumber
) {
}
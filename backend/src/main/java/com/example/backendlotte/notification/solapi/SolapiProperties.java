package com.example.backendlotte.notification.solapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolapiProperties {

    private final String apiKey;
    private final String apiSecret;
    private final String sender;

    public SolapiProperties(
            @Value("${SOLAPI_API_KEY}") String apiKey,
            @Value("${SOLAPI_API_SECRET}") String apiSecret,
            @Value("${SOLAPI_SENDER}") String sender
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.sender = sender;
    }

    public String apiKey() {
        return apiKey;
    }

    public String apiSecret() {
        return apiSecret;
    }

    public String sender() {
        return sender;
    }
}
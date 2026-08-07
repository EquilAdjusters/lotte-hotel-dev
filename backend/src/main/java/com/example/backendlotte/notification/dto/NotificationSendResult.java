package com.example.backendlotte.notification.dto;

public record NotificationSendResult(
    String provider,
    String providerMessageId
) {
}
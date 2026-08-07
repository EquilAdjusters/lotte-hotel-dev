package com.example.backendlotte.notification.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.example.backendlotte.notification.dto.NotificationSendResult;

@Component
@Primary
public class NoOpNotificationSender
        implements NotificationSender {

    @Override
    public NotificationSendResult send(
            String recipient,
            String message
    ) {
        return new NotificationSendResult(
            "NO_OP",
            null
        );
    }
}
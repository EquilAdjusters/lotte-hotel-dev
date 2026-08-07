package com.example.backendlotte.notification.service;

import com.example.backendlotte.notification.dto.NotificationSendResult;

public interface NotificationSender {

    NotificationSendResult send(
        String recipient,
        String message
    );
}
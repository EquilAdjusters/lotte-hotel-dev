package com.example.backendlotte.notification.email;

import com.example.backendlotte.notification.dto.NotificationSendResult;

public interface EmailSender {

    NotificationSendResult send(
        String to,
        String subject,
        String body
    );
}

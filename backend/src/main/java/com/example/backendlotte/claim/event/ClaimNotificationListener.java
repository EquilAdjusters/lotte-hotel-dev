package com.example.backendlotte.claim.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.backendlotte.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClaimNotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleClaimCreated(
            ClaimCreatedEvent event
    ) {
        notificationService
            .sendClaimReceivedNotification(
                event.claimId()
            );
    }
}
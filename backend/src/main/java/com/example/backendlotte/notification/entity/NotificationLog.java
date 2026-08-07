package com.example.backendlotte.notification.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.notification.type.NotificationStatus;
import com.example.backendlotte.notification.type.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private NotificationStatus status;

    @Column(name = "recipient", nullable = false, length = 30)
    private String recipient;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "provider_message_id", length = 255)
    private String providerMessageId;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    private NotificationLog(
            Claim claim,
            NotificationType notificationType,
            String recipient,
            String message
    ) {
        this.claim = claim;
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.message = message;

        this.status = NotificationStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public static NotificationLog pending(
            Claim claim,
            NotificationType notificationType,
            String recipient,
            String message
    ) {
        return new NotificationLog(
            claim,
            notificationType,
            recipient,
            message
        );
    }

    public void success(
            String provider,
            String providerMessageId
    ) {
        this.status = NotificationStatus.SUCCESS;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.failureReason = null;
        this.sentAt = LocalDateTime.now();
    }

    public void fail(
            String provider,
            String failureReason
    ) {
        this.status = NotificationStatus.FAILED;
        this.provider = provider;
        this.failureReason = failureReason;
        this.sentAt = null;
    }
}
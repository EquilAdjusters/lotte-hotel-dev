package com.example.backendlotte.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.notification.dto.NotificationSendResult;
import com.example.backendlotte.notification.entity.NotificationLog;
import com.example.backendlotte.notification.repository.NotificationLogRepository;
import com.example.backendlotte.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ClaimRepository claimRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationSender notificationSender;

    @Transactional
    public void sendClaimReceivedNotification(
            Long claimId
    ) {
        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        String recipient =
            normalizePhone(
                claim.getVictimPhone()
            );

        String message =
            buildClaimReceivedMessage(claim);

        NotificationLog log =
            NotificationLog.pending(
                claim,
                NotificationType.CLAIM_RECEIVED,
                recipient,
                message
            );

        notificationLogRepository.save(log);

        try {
            NotificationSendResult result =
                notificationSender.send(
                    recipient,
                    message
                );

            log.success(
                result.provider(),
                result.providerMessageId()
            );

        } catch (RuntimeException exception) {
            log.fail(
                "UNKNOWN",
                normalizeFailureReason(
                    exception.getMessage()
                )
            );
        }
    }

    private String buildClaimReceivedMessage(
            Claim claim
    ) {
        return """
            [롯데호텔]
            사고 접수가 완료되었습니다.
            접수번호: %s
            담당자가 확인 후 안내드리겠습니다.
            """.formatted(
                claim.getClaimNumber()
            ).trim();
    }

    private String normalizePhone(
            String phone
    ) {
        if (phone == null
                || phone.isBlank()) {
            throw new IllegalArgumentException(
                "피해자 연락처가 없습니다."
            );
        }

        String normalized =
            phone.replaceAll("[^0-9]", "");

        if (normalized.length() < 9
                || normalized.length() > 15) {
            throw new IllegalArgumentException(
                "피해자 연락처 형식이 올바르지 않습니다."
            );
        }

        return normalized;
    }

    private String normalizeFailureReason(
            String message
    ) {
        if (message == null
                || message.isBlank()) {
            return "문자 발송 중 알 수 없는 오류가 발생했습니다.";
        }

        String normalized = message.trim();

        return normalized.length() > 1000
            ? normalized.substring(0, 1000)
            : normalized;
    }
}
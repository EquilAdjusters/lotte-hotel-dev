package com.example.backendlotte.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.notification.dto.NotificationSendResult;
import com.example.backendlotte.notification.entity.NotificationLog;
import com.example.backendlotte.notification.repository.NotificationLogRepository;
import com.example.backendlotte.notification.type.NotificationType;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ClaimRepository claimRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationSender notificationSender;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendClaimReceivedNotification(
            Long claimId
    ) {
        log.info(
            "접수 문자 발송 처리 시작 - claimId={}",
            claimId
        );

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

        NotificationLog notificationLog =
            NotificationLog.pending(
                claim,
                NotificationType.CLAIM_RECEIVED,
                recipient,
                message
            );

        notificationLogRepository.save(notificationLog);

        // 여기서 INSERT를 실제 DB에 바로 날려서
        // DB 문제를 즉시 확인
        notificationLogRepository.flush();

        log.info(
            "문자 발송 로그 PENDING 저장 완료 - notificationLogId={}",
            notificationLog.getId()
        );

        try {
            NotificationSendResult result =
                notificationSender.send(
                    recipient,
                    message
                );

            notificationLog.success(
                result.provider(),
                result.providerMessageId()
            );

            log.info(
                "문자 발송 성공 - claimId={}, notificationLogId={}",
                claimId,
                notificationLog.getId()
            );

        } catch (RuntimeException exception) {

            notificationLog.fail(
                "SOLAPI",
                normalizeFailureReason(
                    exception.getMessage()
                )
            );

            log.error(
                "문자 발송 실패 - claimId={}, notificationLogId={}",
                claimId,
                notificationLog.getId(),
                exception
            );
        }
    }

    private String buildClaimReceivedMessage(
        Claim claim
    ) {
        String branchName =
            claim.getBranch().getName();

        return """
            안녕하세요, 고객님.
            저희는 호텔롯데 %s의 보험업무를 담당하는 와이즈보험중개입니다.

            갑작스러운 사고로 많이 놀라고 불편하셨을 텐데, 진심으로 위로의 말씀을 전합니다.
            현재 호텔롯데 %s에서 고객님의 사고를 보험으로 접수해 드렸습니다. 영업일 기준 2~3일 이내에 전담 손해사정사가 배정되어 고객님께 연락이 갈 예정입니다.

            손해사정사 배정 전이라도 치료나 수리가 필요하시면 먼저 진행하셔도 됩니다. 다만 발생한 치료비 · 수리비의 결제 내역과 세부 영수증은 꼭 보관해 주시기 바랍니다. 이후 보상 처리에 필요합니다.

            고객님의 빠른 쾌유를 진심으로 기원하겠습니다.
            감사합니다.
            """.formatted(
                branchName,
                branchName
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
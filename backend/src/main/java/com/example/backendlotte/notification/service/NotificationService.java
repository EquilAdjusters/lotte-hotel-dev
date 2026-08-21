package com.example.backendlotte.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.type.ClaimType;
import com.example.backendlotte.claim.type.PreferredLanguage;
import com.example.backendlotte.notification.dto.NotificationSendResult;
import com.example.backendlotte.notification.email.EmailSender;
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
    private final EmailSender emailSender;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAdjustingCompanyAssignedEmail(
            Long claimId
    ) {
        log.info(
            "손사배정 이메일 발송 처리 시작 - claimId={}",
            claimId
        );

        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        String recipient = claim.getBranch().getReceiptEmail();

        if (recipient == null
                || recipient.isBlank()) {
            log.warn(
                "손사배정 이메일 발송 건너뜀 - claimId={}, 접수메일 미등록",
                claimId
            );
            return;
        }

        String subject = buildAssignedEmailSubject(claim);
        String body = buildAssignedEmailBody(claim);

        NotificationLog notificationLog =
            NotificationLog.pending(
                claim,
                NotificationType.ADJUSTING_COMPANY_ASSIGNED,
                recipient,
                body
            );

        notificationLogRepository.save(notificationLog);
        notificationLogRepository.flush();

        try {
            NotificationSendResult result =
                emailSender.send(
                    recipient,
                    subject,
                    body
                );

            notificationLog.success(
                result.provider(),
                result.providerMessageId()
            );

            log.info(
                "손사배정 이메일 발송 성공 - claimId={}, notificationLogId={}",
                claimId,
                notificationLog.getId()
            );

        } catch (RuntimeException exception) {

            notificationLog.fail(
                "SMTP",
                normalizeFailureReason(
                    exception.getMessage()
                )
            );

            log.error(
                "손사배정 이메일 발송 실패 - claimId={}, notificationLogId={}",
                claimId,
                notificationLog.getId(),
                exception
            );
        }
    }

    private String buildAssignedEmailSubject(
        Claim claim
    ) {
        return "[호텔롯데 클레임 접수] %s 배정요청".formatted(
            claim.getAdjustingCompany().getName()
        );
    }

    private String buildAssignedEmailBody(
        Claim claim
    ) {
        String adjustingCompanyName =
            claim.getAdjustingCompany().getName();

        String claimTypeLabel =
            claim.getClaimType() == ClaimType.LIABILITY
                ? "배상"
                : "재물";

        return """
            안녕하세요 와이즈 보험중개입니다.
            계약자 요청에 따라 하기건은 %s으로 배정 요청드리며, 빠른 처리부탁드립니다.
            사고내용 :  %s
            사고자명 : %s
            연락처 : %s
            사고내용 :  %s
            """.formatted(
                adjustingCompanyName,
                claimTypeLabel,
                claim.getVictimName(),
                claim.getVictimPhone(),
                claim.getAccidentDescription()
            ).trim();
    }

    private String buildClaimReceivedMessage(
        Claim claim
    ) {
        String branchName =
            claim.getBranch().getName();

        PreferredLanguage language =
            claim.getPreferredLanguage() != null
                ? claim.getPreferredLanguage()
                : PreferredLanguage.KOREAN;

        return switch (language) {
            case ENGLISH -> buildClaimReceivedMessageEnglish(branchName);
            case CHINESE -> buildClaimReceivedMessageChinese(branchName);
            case JAPANESE -> buildClaimReceivedMessageJapanese(branchName);
            case KOREAN -> buildClaimReceivedMessageKorean(branchName);
        };
    }

    private String buildClaimReceivedMessageKorean(
        String branchName
    ) {
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

    private String buildClaimReceivedMessageEnglish(
        String branchName
    ) {
        return """
            Hello,
            We are WISE Insurance Brokerage, in charge of insurance matters for Lotte Hotel %s.

            We are truly sorry to hear about your sudden accident and any inconvenience it may have caused. Please accept our sincere condolences.

            Lotte Hotel %s has registered your accident with our insurance. A dedicated claims adjuster will be assigned and will contact you within 2-3 business days.

            Even before an adjuster is assigned, you may proceed with any necessary treatment or repairs. However, please be sure to keep all payment records and detailed receipts for medical or repair costs, as they will be needed for the compensation process.

            We sincerely wish you a speedy recovery.
            Thank you.
            """.formatted(
                branchName,
                branchName
            ).trim();
    }

    private String buildClaimReceivedMessageChinese(
        String branchName
    ) {
        return """
            您好。
            我们是负责乐天酒店%s保险业务的WISE保险经纪公司。

            对于突发事故给您带来的惊吓和不便，我们深表慰问。

            目前乐天酒店%s已为您办理事故保险登记。我们将在2~3个工作日内（以营业日为准）指派专属损害查定师与您联系。

            在指派损害查定师之前，如需先行治疗或维修，您可以先行处理。但请务必保留所产生的治疗费、维修费的付款明细及详细收据，后续理赔时需要用到。

            衷心祝愿您早日康复。
            谢谢。
            """.formatted(
                branchName,
                branchName
            ).trim();
    }

    private String buildClaimReceivedMessageJapanese(
        String branchName
    ) {
        return """
            お客様
            私どもは、ロッテホテル%sの保険業務を担当しているワイズ保険仲介です。

            突然の事故で驚かれ、ご不便をおかけしましたこと、心よりお見舞い申し上げます。

            現在、ロッテホテル%sにてお客様の事故を保険にてご登録いたしました。営業日基準で2〜3日以内に専任の損害査定士が割り当てられ、ご連絡いたします。

            損害査定士の割り当て前でも、治療や修理が必要な場合は先に進めていただいて構いません。ただし、発生した治療費・修理費のお支払い内訳および領収書は必ず保管しておいてください。今後の補償手続きに必要となります。

            お客様の一日も早いご回復を心よりお祈り申し上げます。
            ありがとうございます。
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
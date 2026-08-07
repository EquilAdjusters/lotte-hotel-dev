package com.example.backendlotte.claim.entity;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimHistorySource;
import com.example.backendlotte.claim.type.ClaimHistoryType;
import com.example.backendlotte.claim.type.ClaimStatus;

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
@Table(name = "claim_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "claim_id",
        nullable = false
    )
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_account_id")
    private Account actorAccount;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "history_type",
        nullable = false,
        length = 30
    )
    private ClaimHistoryType historyType;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "source_type",
        nullable = false,
        length = 30
    )
    private ClaimHistorySource sourceType;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "previous_status",
        length = 30
    )
    private ClaimStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "current_status",
        length = 30
    )
    private ClaimStatus currentStatus;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "closing_result",
        length = 30
    )
    private ClaimClosingResult closingResult;

    @Column(
        name = "description",
        nullable = false,
        length = 500
    )
    private String description;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        insertable = false
    )
    private java.time.LocalDateTime createdAt;

    private ClaimHistory(
            Claim claim,
            Account actorAccount,
            ClaimHistoryType historyType,
            ClaimHistorySource sourceType,
            ClaimStatus previousStatus,
            ClaimStatus currentStatus,
            ClaimClosingResult closingResult,
            String description
    ) {
        this.claim = claim;
        this.actorAccount = actorAccount;
        this.historyType = historyType;
        this.sourceType = sourceType;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.closingResult = closingResult;
        this.description = description;
    }

    public static ClaimHistory created(
            Claim claim,
            Account actorAccount
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "접수건 정보는 필수입니다."
            );
        }

        if (actorAccount == null) {
            throw new IllegalArgumentException(
                "접수 계정 정보는 필수입니다."
            );
        }

        return new ClaimHistory(
            claim,
            actorAccount,
            ClaimHistoryType.CREATED,
            ClaimHistorySource.USER,
            null,
            ClaimStatus.RECEIVED,
            null,
            "사고접수 생성"
        );
    }

    public static ClaimHistory statusChangedBySystem(
            Claim claim,
            ClaimStatus previousStatus,
            ClaimStatus currentStatus,
            String description
    ) {
        validateStatusChange(
            claim,
            previousStatus,
            currentStatus
        );

        return new ClaimHistory(
            claim,
            null,
            ClaimHistoryType.STATUS_CHANGED,
            ClaimHistorySource.SYSTEM,
            previousStatus,
            currentStatus,
            null,
            normalizeDescription(description)
        );
    }

    public static ClaimHistory closedByExternalAdapter(
            Claim claim,
            ClaimStatus previousStatus,
            ClaimClosingResult closingResult,
            String description
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "접수건 정보는 필수입니다."
            );
        }

        if (previousStatus == null) {
            throw new IllegalArgumentException(
                "변경 전 상태는 필수입니다."
            );
        }

        if (closingResult == null) {
            throw new IllegalArgumentException(
                "종결 결과는 필수입니다."
            );
        }

        return new ClaimHistory(
            claim,
            null,
            ClaimHistoryType.CLOSED,
            ClaimHistorySource.EXTERNAL_ADAPTER,
            previousStatus,
            ClaimStatus.CLOSED,
            closingResult,
            normalizeDescription(description)
        );
    }

    private static void validateStatusChange(
            Claim claim,
            ClaimStatus previousStatus,
            ClaimStatus currentStatus
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "접수건 정보는 필수입니다."
            );
        }

        if (previousStatus == null
                || currentStatus == null) {
            throw new IllegalArgumentException(
                "상태 변경 전·후 값은 필수입니다."
            );
        }

        if (previousStatus == currentStatus) {
            throw new IllegalArgumentException(
                "변경 전 상태와 변경 후 상태가 같습니다."
            );
        }
    }

    private static String normalizeDescription(
            String description
    ) {
        if (description == null
                || description.isBlank()) {
            throw new IllegalArgumentException(
                "이력 설명은 필수입니다."
            );
        }

        String normalized = description.trim();

        if (normalized.length() > 500) {
            throw new IllegalArgumentException(
                "이력 설명은 500자 이하로 입력해주세요."
            );
        }

        return normalized;
    }
}
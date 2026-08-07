package com.example.backendlotte.claim.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.type.ConsentMethod;
import com.example.backendlotte.claim.type.ConsentStatus;
import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim_consents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimConsent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 접수건 하나당 동의 정보는 하나만 존재한다.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "claim_id",
        nullable = false,
        unique = true
    )
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "consent_status",
        nullable = false,
        length = 30
    )
    private ConsentStatus consentStatus;

    @Column(name = "consent_obtained_at")
    private LocalDateTime consentObtainedAt;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "consent_method",
        length = 30
    )
    private ConsentMethod consentMethod;

    private ClaimConsent(
            Claim claim,
            ConsentStatus consentStatus,
            LocalDateTime consentObtainedAt,
            ConsentMethod consentMethod
    ) {
        validateConsent(
            consentStatus,
            consentObtainedAt,
            consentMethod
        );

        this.claim = claim;
        this.consentStatus = consentStatus;
        this.consentObtainedAt = consentObtainedAt;
        this.consentMethod = consentMethod;
    }

    public static ClaimConsent create(
            Claim claim,
            ConsentStatus consentStatus,
            LocalDateTime consentObtainedAt,
            ConsentMethod consentMethod
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "접수건 정보는 필수입니다."
            );
        }

        return new ClaimConsent(
            claim,
            consentStatus,
            consentObtainedAt,
            consentMethod
        );
    }

    /**
     * 동의 취득 상태로 변경하거나 동의 정보를 수정한다.
     */
    public void obtainConsent(
            LocalDateTime consentObtainedAt,
            ConsentMethod consentMethod
    ) {
        validateConsent(
            ConsentStatus.OBTAINED,
            consentObtainedAt,
            consentMethod
        );

        this.consentStatus = ConsentStatus.OBTAINED;
        this.consentObtainedAt = consentObtainedAt;
        this.consentMethod = consentMethod;
    }

    /**
     * 현장 동의 미취득 상태로 변경한다.
     */
    public void markNotObtained() {
        this.consentStatus = ConsentStatus.NOT_OBTAINED;
        this.consentObtainedAt = null;
        this.consentMethod = null;
    }

    private static void validateConsent(
            ConsentStatus consentStatus,
            LocalDateTime consentObtainedAt,
            ConsentMethod consentMethod
    ) {
        if (consentStatus == null) {
            throw new IllegalArgumentException(
                "개인정보 동의 상태는 필수입니다."
            );
        }

        if (consentStatus == ConsentStatus.OBTAINED) {
            if (consentObtainedAt == null) {
                throw new IllegalArgumentException(
                    "동의 취득 일시는 필수입니다."
                );
            }

            if (consentMethod == null) {
                throw new IllegalArgumentException(
                    "동의 취득 방법은 필수입니다."
                );
            }

            return;
        }

        if (consentObtainedAt != null || consentMethod != null) {
            throw new IllegalArgumentException(
                "미동의 상태에는 동의 취득 일시와 방법을 입력할 수 없습니다."
            );
        }
    }
}
package com.example.backendlotte.claim.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.type.ConsentMethod;
import com.example.backendlotte.claim.type.ConsentStatus;

import jakarta.validation.constraints.NotNull;

public record ClaimConsentRequest(

    @NotNull(message = "개인정보 동의 상태는 필수입니다.")
    ConsentStatus consentStatus,

    LocalDateTime consentObtainedAt,

    ConsentMethod consentMethod

) {
}
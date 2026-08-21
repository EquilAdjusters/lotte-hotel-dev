package com.example.backendlotte.claim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimConsent;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimStatus;
import com.example.backendlotte.claim.type.ClaimType;
import com.example.backendlotte.claim.type.ConsentMethod;
import com.example.backendlotte.claim.type.ConsentStatus;
import com.example.backendlotte.claim.type.PreferredLanguage;
import com.example.backendlotte.claim.type.VictimType;
import com.example.backendlotte.global.util.PersonalDataMasker;

public record ClaimResponse(

    Long id,
    String claimNumber,

    Long hotelCompanyId,
    String hotelCompanyName,

    Long hotelId,
    String hotelName,

    Long branchId,
    String branchName,

    Long createdByAccountId,
    String createdByLoginId,

    String victimName,
    String victimPhone,
    LocalDate victimBirthDate,
    String victimPhoneMasked,
    String victimBirthDateMasked,
    VictimType victimType,
    PreferredLanguage preferredLanguage,

    String residenceSido,
    String residenceSigungu,
    String residenceDetail,

    ClaimType claimType,
    LocalDateTime accidentAt,
    String accidentDescription,

    String receivedByName,
    String receivedByExtension,

    ConsentStatus consentStatus,
    LocalDateTime consentObtainedAt,
    ConsentMethod consentMethod,

    ClaimStatus status,
    ClaimClosingResult closingResult,
    LocalDateTime closedAt,

    LocalDateTime createdAt,
    LocalDateTime updatedAt,
            
    String adjustingCompanyName,
    String adjusterName,
    String adjusterPhone

) {

    public static ClaimResponse from(
            Claim claim,
            ClaimConsent consent
    ) {
        return new ClaimResponse(
            claim.getId(),
            claim.getClaimNumber(),

            claim.getHotelCompany().getId(),
            claim.getHotelCompany().getName(),

            claim.getHotel().getId(),
            claim.getHotel().getName(),

            claim.getBranch().getId(),
            claim.getBranch().getName(),

            claim.getCreatedByAccount().getId(),
            claim.getCreatedByAccount().getLoginId(),

            claim.getVictimName(),
            claim.getVictimPhone(),
            claim.getVictimBirthDate(),
            PersonalDataMasker.maskPhone(claim.getVictimPhone()),
            PersonalDataMasker.maskBirthDate(claim.getVictimBirthDate()),
            claim.getVictimType(),
            claim.getPreferredLanguage(),

            claim.getResidenceSido(),
            claim.getResidenceSigungu(),
            claim.getResidenceDetail(),

            claim.getClaimType(),
            claim.getAccidentAt(),
            claim.getAccidentDescription(),

            claim.getReceivedByName(),
            claim.getReceivedByExtension(),

            consent.getConsentStatus(),
            consent.getConsentObtainedAt(),
            consent.getConsentMethod(),

            claim.getStatus(),
            claim.getClosingResult(),
            claim.getClosedAt(),

            claim.getCreatedAt(),
            claim.getUpdatedAt(),
                    
            claim.getAdjustingCompany() != null
            ? claim.getAdjustingCompany().getName()
            : null,

            claim.getAdjuster() != null
                ? claim.getAdjuster().getName()
                : null,

            claim.getAdjuster() != null
                ? claim.getAdjuster().getPhone()
                : null
        );
    }
}
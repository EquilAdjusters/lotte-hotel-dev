package com.example.backendlotte.adjusting.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.Claim;

public record ClaimAssignmentListResponse(

    Long claimId,
    String claimNumber,

    LocalDateTime receivedAt,
    LocalDateTime accidentAt,

    String hotelName,
    String branchName,

    String victimName,
    String receivedByName,

    String adjustingCompanyName,
    String adjusterName,
    String adjusterPhone,

    LocalDateTime assignedAt,
    boolean assigned,
            
    String insuranceCompanyName,
    String receiptEmail
) {

    public static ClaimAssignmentListResponse from(
            Claim claim
    ) {
        return new ClaimAssignmentListResponse(
            claim.getId(),
            claim.getClaimNumber(),

            claim.getCreatedAt(),
            claim.getAccidentAt(),

            claim.getHotel().getName(),
            claim.getBranch().getName(),

            claim.getVictimName(),
            claim.getReceivedByName(),

            claim.getAdjustingCompany() != null
                ? claim.getAdjustingCompany().getName()
                : null,

            claim.getAdjuster() != null
                ? claim.getAdjuster().getName()
                : null,

            claim.getAdjuster() != null
                ? claim.getAdjuster().getPhone()
                : null,

            claim.getAssignedAt(),

            claim.getAdjustingCompany() != null
                && claim.getAdjuster() != null,
            
            claim.getBranch().getInsuranceCompany() != null
            ? claim.getBranch().getInsuranceCompany().getName()
            : null,

            claim.getBranch().getReceiptEmail()
        );
    }
}
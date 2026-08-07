package com.example.backendlotte.claim.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.type.ClaimStatus;

public record ClaimDuplicateResponse(
    Long claimId,
    String claimNumber,
    String victimName,
    LocalDateTime accidentAt,
    String hotelName,
    String branchName,
    ClaimStatus status
) {

    public static ClaimDuplicateResponse from(
            Claim claim
    ) {
        return new ClaimDuplicateResponse(
            claim.getId(),
            claim.getClaimNumber(),
            claim.getVictimName(),
            claim.getAccidentAt(),
            claim.getHotel().getName(),
            claim.getBranch().getName(),
            claim.getStatus()
        );
    }
}
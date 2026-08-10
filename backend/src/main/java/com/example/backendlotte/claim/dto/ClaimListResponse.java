package com.example.backendlotte.claim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimProgressStatus;
import com.example.backendlotte.claim.type.ClaimStatus;

public record ClaimListResponse(

    Long claimId,
    String claimNumber,

    LocalDateTime receivedAt,
    LocalDateTime accidentAt,

    String receivedByName,
    String victimName,
    LocalDate victimBirthDate,

    ClaimProgressStatus progressStatus

) {

    public static ClaimListResponse from(
            Claim claim
    ) {
        return new ClaimListResponse(
            claim.getId(),
            claim.getClaimNumber(),

            claim.getCreatedAt(),
            claim.getAccidentAt(),

            claim.getReceivedByName(),
            claim.getVictimName(),
            claim.getVictimBirthDate(),

            resolveProgressStatus(claim)
        );
    }

    private static ClaimProgressStatus resolveProgressStatus(
            Claim claim
    ) {
        if (claim.getStatus() == ClaimStatus.IN_PROGRESS) {
            return ClaimProgressStatus.IN_PROGRESS;
        }

        if (claim.getStatus() == ClaimStatus.CLOSED) {

            if (claim.getClosingResult()
                    == ClaimClosingResult.INSURANCE_PAID) {
                return ClaimProgressStatus.CLOSED_PAID;
            }

            if (claim.getClosingResult()
                    == ClaimClosingResult.EXEMPTED) {
                return ClaimProgressStatus.CLOSED_EXEMPTED;
            }
        }

        throw new IllegalStateException(
            "처리할 수 없는 사고 진행상태입니다."
        );
    }
}
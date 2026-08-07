package com.example.backendlotte.claim.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.ClaimHistory;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimHistorySource;
import com.example.backendlotte.claim.type.ClaimHistoryType;
import com.example.backendlotte.claim.type.ClaimStatus;

public record ClaimHistoryResponse(

    Long id,
    Long claimId,

    Long actorAccountId,
    String actorLoginId,

    ClaimHistoryType historyType,
    ClaimHistorySource sourceType,

    ClaimStatus previousStatus,
    ClaimStatus currentStatus,

    ClaimClosingResult closingResult,

    String previousValue,
    String currentValue,

    String description,
    LocalDateTime createdAt

) {

    public static ClaimHistoryResponse from(
            ClaimHistory history
    ) {
        return new ClaimHistoryResponse(
            history.getId(),

            history.getClaim().getId(),

            history.getActorAccount() == null
                ? null
                : history.getActorAccount().getId(),

            history.getActorAccount() == null
                ? null
                : history.getActorAccount().getLoginId(),

            history.getHistoryType(),
            history.getSourceType(),

            history.getPreviousStatus(),
            history.getCurrentStatus(),

            history.getClosingResult(),

            history.getPreviousValue(),
            history.getCurrentValue(),

            history.getDescription(),
            history.getCreatedAt()
        );
    }
}
package com.example.backendlotte.account.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.account.entity.AccountHistory;
import com.example.backendlotte.account.type.AccountHistoryType;

public record AccountHistoryResponse(
    Long id,
    Long accountId,
    String accountLoginId,
    Long actorAccountId,
    String actorLoginId,
    AccountHistoryType actionType,
    String beforeValue,
    String afterValue,
    String description,
    String actorIp,
    LocalDateTime createdAt
) {

    public static AccountHistoryResponse from(AccountHistory history) {
        return new AccountHistoryResponse(
            history.getId(),

            history.getAccount() == null
                ? null : history.getAccount().getId(),

            history.getAccount() == null
                ? null : history.getAccount().getLoginId(),

            history.getActorAccount() == null
                ? null : history.getActorAccount().getId(),

            history.getActorAccount() == null
                ? null : history.getActorAccount().getLoginId(),

            history.getActionType(),
            history.getBeforeValue(),
            history.getAfterValue(),
            history.getDescription(),
            history.getActorIp(),
            history.getCreatedAt()
        );
    }
}
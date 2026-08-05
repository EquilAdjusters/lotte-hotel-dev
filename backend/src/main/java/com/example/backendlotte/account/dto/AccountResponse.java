package com.example.backendlotte.account.dto;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

public record AccountResponse(
    Long id,
    String loginId,
    String displayName,
    Role role,
    ScopeType scopeType,
    AccountStatus status,
    boolean sharedAccount
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getLoginId(),
            account.getDisplayName(),
            account.getRole(),
            account.getScopeType(),
            account.getStatus(),
            account.isSharedAccount()
        );
    }
}
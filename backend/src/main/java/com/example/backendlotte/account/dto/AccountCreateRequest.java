package com.example.backendlotte.account.dto;

import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

public record AccountCreateRequest(
    String loginId,
    String password,
    String displayName,
    Role role,
    ScopeType scopeType,
    boolean sharedAccount
) {
}
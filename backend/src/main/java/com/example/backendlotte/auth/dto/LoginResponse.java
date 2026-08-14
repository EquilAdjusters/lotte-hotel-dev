package com.example.backendlotte.auth.dto;

import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

public record LoginResponse(
    Long accountId,
    String loginId,
    String displayName,
    Role role,
    ScopeType scopeType,
    String hotelCompanyName,
    String hotelName,
    String branchName
) {

}

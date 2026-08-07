package com.example.backendlotte.account.dto;

import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;

public record AccountSearchCondition(
    String keyword,
    Role role,
    AccountStatus status,
    Long hotelCompanyId,
    Long hotelId,
    Long branchId,
    Long branchGroupId
) {
}
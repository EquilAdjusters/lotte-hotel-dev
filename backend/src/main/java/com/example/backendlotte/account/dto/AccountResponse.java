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
    boolean sharedAccount,

    Long hotelCompanyId,
    String hotelCompanyName,

    Long hotelId,
    String hotelName,

    Long branchId,
    String branchName,

    Long branchGroupId,
    String branchGroupName
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getLoginId(),
            account.getDisplayName(),
            account.getRole(),
            account.getScopeType(),
            account.getStatus(),
            account.isSharedAccount(),

            account.getHotelCompany() == null
                ? null
                : account.getHotelCompany().getId(),

            account.getHotelCompany() == null
                ? null
                : account.getHotelCompany().getName(),

            account.getHotel() == null
                ? null
                : account.getHotel().getId(),

            account.getHotel() == null
                ? null
                : account.getHotel().getName(),

            account.getBranch() == null
                ? null
                : account.getBranch().getId(),

            account.getBranch() == null
                ? null
                : account.getBranch().getName(),

            account.getBranchGroup() == null
                ? null
                : account.getBranchGroup().getId(),

            account.getBranchGroup() == null
                ? null
                : account.getBranchGroup().getName()
        );
    }
}
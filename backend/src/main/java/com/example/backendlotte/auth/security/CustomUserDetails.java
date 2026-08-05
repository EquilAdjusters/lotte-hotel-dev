package com.example.backendlotte.auth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

public class CustomUserDetails implements UserDetails {

    private final Long accountId;
    private final String loginId;
    private final String passwordHash;
    private final String displayName;
    private final Role role;
    private final ScopeType scopeType;
    private final AccountStatus status;
    private final boolean sharedAccount;

    private final Long hotelCompanyId;
    private final Long hotelId;
    private final Long branchId;
    private final Long branchGroupId;

    public CustomUserDetails(Account account) {
        this.accountId = account.getId();
        this.loginId = account.getLoginId();
        this.passwordHash = account.getPasswordHash();
        this.displayName = account.getDisplayName();
        this.role = account.getRole();
        this.scopeType = account.getScopeType();
        this.status = account.getStatus();
        this.sharedAccount = account.isSharedAccount();

        this.hotelCompanyId = account.getHotelCompany() == null
                ? null
                : account.getHotelCompany().getId();

        this.hotelId = account.getHotel() == null
                ? null
                : account.getHotel().getId();

        this.branchId = account.getBranch() == null
                ? null
                : account.getBranch().getId();

        this.branchGroupId = account.getBranchGroup() == null
                ? null
                : account.getBranchGroup().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != AccountStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Role getRole() {
        return role;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public boolean isSharedAccount() {
        return sharedAccount;
    }

    public Long getHotelCompanyId() {
        return hotelCompanyId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Long getBranchGroupId() {
        return branchGroupId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CustomUserDetails other)) {
            return false;
        }

        return accountId != null && accountId.equals(other.accountId);
    }

    @Override
    public int hashCode() {
        return accountId == null ? 0 : accountId.hashCode();
    }
}
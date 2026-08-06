package com.example.backendlotte.account.entity;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.BranchGroup;
import com.example.backendlotte.organization.entity.HotelCompany;

import java.time.LocalDateTime;

import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;
import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인할 때 사용하는 아이디
    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String loginId;

    // BCrypt 등으로 암호화된 비밀번호만 저장
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // 관리자명 또는 계정 표시명
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 30)
    private ScopeType scopeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status;

    @Column(name = "shared_account", nullable = false)
    private boolean sharedAccount;

    // ADMIN3 등에 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_company_id")
    private HotelCompany hotelCompany;

    // 필요할 경우 특정 호텔 단위 관리에 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    // 지점 공유계정에 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // ADMIN4 권역 계정에 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_group_id")
    private BranchGroup branchGroup;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at", nullable = false)
    private LocalDateTime passwordChangedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Account(
        String loginId,
        String passwordHash,
        String displayName,
        Role role,
        ScopeType scopeType,
        AccountStatus status,
        boolean sharedAccount,
        HotelCompany hotelCompany,
        Hotel hotel,
        Branch branch,
        BranchGroup branchGroup,
        LocalDateTime passwordChangedAt
    ) {
        this.loginId = loginId;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
        this.scopeType = scopeType;
        this.status = status;
        this.sharedAccount = sharedAccount;

        this.hotelCompany = hotelCompany;
        this.hotel = hotel;
        this.branch = branch;
        this.branchGroup = branchGroup;

        this.failedLoginCount = 0;
        this.passwordChangedAt = passwordChangedAt;
    }

    public static Account createAdmin1(
            String loginId,
            String passwordHash,
            String displayName
    ) {
        return new Account(
            loginId,
            passwordHash,
            displayName,
            Role.ADMIN1,
            ScopeType.ALL,
            AccountStatus.ACTIVE,
            false,
            null,
            null,
            null,
            null,
            LocalDateTime.now()
        );
    }

    public static Account create(
        String loginId,
        String passwordHash,
        String displayName,
        Role role,
        ScopeType scopeType,
        boolean sharedAccount,
        HotelCompany hotelCompany,
        Hotel hotel,
        Branch branch,
        BranchGroup branchGroup
    ) {
        return new Account(
            loginId,
            passwordHash,
            displayName,
            role,
            scopeType,
            AccountStatus.ACTIVE,
            sharedAccount,
            hotelCompany,
            hotel,
            branch,
            branchGroup,
            LocalDateTime.now()
        );
    }
    
    public void loginSucceeded() {
        this.failedLoginCount = 0;
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean loginFailed(int maxFailures) {
        if (this.status == AccountStatus.LOCKED) {
            return true;
        }

        this.failedLoginCount++;

        if (this.failedLoginCount >= maxFailures) {
            this.status = AccountStatus.LOCKED;
            this.lockedAt = LocalDateTime.now();
            return true;
        }

        return false;
    }

    public void unlock() {
        this.status = AccountStatus.ACTIVE;
        this.failedLoginCount = 0;
        this.lockedAt = null;
    }

    public void updateAffiliation(
        HotelCompany hotelCompany,
        Hotel hotel,
        Branch branch,
        BranchGroup branchGroup
    ) {
        this.hotelCompany = hotelCompany;
        this.hotel = hotel;
        this.branch = branch;
        this.branchGroup = branchGroup;
    }
}
package com.example.backendlotte.account.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.account.type.AccountHistoryType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;

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

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "account_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 변경 대상 계정
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 변경 작업을 수행한 계정
    // 초기 시스템 등록이나 배치 작업은 수행 계정이 없을 수 있으므로 nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_account_id")
    private Account actorAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private AccountHistoryType actionType;

    // 변경 전 값. JSON 문자열로 저장할 예정
    @Column(name = "before_value", columnDefinition = "TEXT")
    private String beforeValue;

    // 변경 후 값. JSON 문자열로 저장할 예정
    @Column(name = "after_value", columnDefinition = "TEXT")
    private String afterValue;

    @Column(length = 1000)
    private String description;

    @Column(name = "actor_ip", length = 45)
    private String actorIp;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private AccountHistory(
        Account account,
        Account actorAccount,
        AccountHistoryType actionType,
        String beforeValue,
        String afterValue,
        String description,
        String actorIp
    ) {
        this.account = account;
        this.actorAccount = actorAccount;
        this.actionType = actionType;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
        this.description = description;
        this.actorIp = actorIp;
    }

    public static AccountHistory created(
            Account account,
            Account actorAccount,
            String afterValue,
            String actorIp
    ) {
        return new AccountHistory(
                account,
                actorAccount,
                AccountHistoryType.CREATED,
                null,
                afterValue,
                "계정 발급",
                actorIp);
    }
    
    public static AccountHistory updated(
        Account account,
        Account actorAccount,
        String beforeValue,
        String afterValue,
        String actorIp
    ) {
        return new AccountHistory(
                account,
                actorAccount,
                AccountHistoryType.UPDATED,
                beforeValue,
                afterValue,
                "계정 정보 수정",
                actorIp);
    }
    
    public static AccountHistory passwordChanged(
        Account account,
        Account actorAccount,
        String actorIp
    ) {
        return new AccountHistory(
            account,
            actorAccount,
            AccountHistoryType.PASSWORD_CHANGED,
            null,
            null,
            "본인 비밀번호 변경",
            actorIp
        );
    }

    public static AccountHistory passwordReset(
            Account account,
            Account actorAccount,
            String actorIp
    ) {
        return new AccountHistory(
            account,
            actorAccount,
            AccountHistoryType.PASSWORD_RESET,
            null,
            null,
            "관리자 비밀번호 초기화",
            actorIp
        );
    }
}
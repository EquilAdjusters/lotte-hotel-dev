package com.example.backendlotte.account.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.account.type.LoginFailureReason;

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
@Table(name = "login_access_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인에 성공하거나 아이디와 계정이 확인된 경우 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 존재하지 않는 아이디로 시도한 경우도 기록하기 위해 별도 저장
    @Column(name = "attempted_login_id", nullable = false, length = 50)
    private String attemptedLoginId;

    @Column(nullable = false)
    private boolean success;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 50)
    private LoginFailureReason failureReason;

    // IPv4 및 IPv6 고려
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // 성공한 로그인 세션을 추적할 때 사용
    @Column(name = "session_id", length = 128)
    private String sessionId;

    @Column(name = "login_at", nullable = false, updatable = false)
    private LocalDateTime loginAt;

    @Column(name = "logout_at")
    private LocalDateTime logoutAt;

    private LoginAccessLog(
        Account account,
        String attemptedLoginId,
        boolean success,
        LoginFailureReason failureReason,
        String ipAddress,
        String userAgent,
        String sessionId,
        LocalDateTime loginAt
    ) {
        this.account = account;
        this.attemptedLoginId = attemptedLoginId;
        this.success = success;
        this.failureReason = failureReason;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.loginAt = loginAt;
    }

    public static LoginAccessLog success(
            Account account,
            String ipAddress,
            String userAgent,
            String sessionId
    ) {
        return new LoginAccessLog(
            account,
            account.getLoginId(),
            true,
            null,
            ipAddress,
            userAgent,
            sessionId,
            LocalDateTime.now()
        );
    }

    public static LoginAccessLog failure(
            Account account,
            String attemptedLoginId,
            LoginFailureReason failureReason,
            String ipAddress,
            String userAgent
    ) {
        return new LoginAccessLog(
            account,
            attemptedLoginId,
            false,
            failureReason,
            ipAddress,
            userAgent,
            null,
            LocalDateTime.now()
        );
    }

    public void logout() {
        this.logoutAt = LocalDateTime.now();
    }


}
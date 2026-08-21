package com.example.backendlotte.account.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

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

    /*
     * 이 기능 도입 이전 기록에 표시해 두는 값.
     * 체인 검증 시작점으로 취급하지 않고 건너뛴다.
     */
    public static final String LEGACY_UNCHAINED = "LEGACY-UNCHAINED";

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

    /*
     * 위·변조 방지용 해시체인.
     * previousHash는 직전 기록의 recordHash를 그대로 물려받고,
     * recordHash는 이 기록의 불변 필드 + previousHash를 해시한 값이다.
     * 이후 로그아웃 시각 갱신처럼 정상적으로 변경되는 값은 해시 대상에서 제외한다.
     */
    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "record_hash", nullable = false, length = 64)
    private String recordHash;

    private LoginAccessLog(
        Account account,
        String attemptedLoginId,
        boolean success,
        LoginFailureReason failureReason,
        String ipAddress,
        String userAgent,
        String sessionId,
        LocalDateTime loginAt,
        String previousHash
    ) {
        this.account = account;
        this.attemptedLoginId = attemptedLoginId;
        this.success = success;
        this.failureReason = failureReason;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.loginAt = loginAt;
        this.previousHash = previousHash;
        this.recordHash = computeHash(
            previousHash,
            account != null ? account.getId() : null,
            attemptedLoginId,
            success,
            failureReason,
            ipAddress,
            userAgent,
            sessionId,
            loginAt
        );
    }

    public static LoginAccessLog success(
            Account account,
            String ipAddress,
            String userAgent,
            String sessionId,
            String previousHash
    ) {
        return new LoginAccessLog(
            account,
            account.getLoginId(),
            true,
            null,
            ipAddress,
            userAgent,
            sessionId,
            LocalDateTime.now(),
            previousHash
        );
    }

    public static LoginAccessLog failure(
            Account account,
            String attemptedLoginId,
            LoginFailureReason failureReason,
            String ipAddress,
            String userAgent,
            String previousHash
    ) {
        return new LoginAccessLog(
            account,
            attemptedLoginId,
            false,
            failureReason,
            ipAddress,
            userAgent,
            null,
            LocalDateTime.now(),
            previousHash
        );
    }

    public void logout() {
        this.logoutAt = LocalDateTime.now();
    }

    /*
     * 체인 검증 서비스에서 저장된 값과 재계산 값을 비교할 때도 사용하는
     * 공용 해시 계산 로직. 여기서 값 하나만 바뀌어도 해시가 달라진다.
     */
    public static String computeHash(
            String previousHash,
            Long accountId,
            String attemptedLoginId,
            boolean success,
            LoginFailureReason failureReason,
            String ipAddress,
            String userAgent,
            String sessionId,
            LocalDateTime loginAt
    ) {
        String payload = String.join(
            "|",
            previousHash == null ? "" : previousHash,
            accountId == null ? "" : accountId.toString(),
            attemptedLoginId == null ? "" : attemptedLoginId,
            Boolean.toString(success),
            failureReason == null ? "" : failureReason.name(),
            ipAddress == null ? "" : ipAddress,
            userAgent == null ? "" : userAgent,
            sessionId == null ? "" : sessionId,
            loginAt == null ? "" : loginAt.toString()
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                payload.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                "해시 알고리즘을 사용할 수 없습니다.",
                exception
            );
        }
    }
}

package com.example.backendlotte.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.entity.LoginAccessLog;
import com.example.backendlotte.account.repository.AccountHistoryRepository;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.repository.LoginAccessLogRepository;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.LoginFailureReason;
import com.example.backendlotte.account.entity.AccountHistory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final AccountRepository accountRepository;
    private final LoginAccessLogRepository loginAccessLogRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    @Value("${app.security.login.max-failures:5}")
    private int maxFailures;

    @Transactional
    public void recordSuccess(
            Long accountId,
            String ipAddress,
            String userAgent,
            String sessionId
    ) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException("계정을 찾을 수 없습니다.")
            );

        account.loginSucceeded();

        loginAccessLogRepository.save(
            LoginAccessLog.success(
                account,
                ipAddress,
                userAgent,
                sessionId,
                latestRecordHash()
            )
        );
    }

    @Transactional
    public boolean recordFailure(
            Long accountId,
            String attemptedLoginId,
            LoginFailureReason reason,
            String ipAddress,
            String userAgent
    ) {
        Account account = null;
        boolean newlyLocked = false;

        if (accountId != null) {
            account = accountRepository
                .findById(accountId)
                .orElse(null);

            if (account != null) {
                boolean wasLocked =
                    account.getStatus() == AccountStatus.LOCKED;

                boolean locked =
                    account.loginFailed(maxFailures);

                newlyLocked = !wasLocked && locked;

                if (newlyLocked) {
                    accountHistoryRepository.save(
                        AccountHistory.locked(
                            account,
                            ipAddress
                        )
                    );
                }
            }
        }

        loginAccessLogRepository.save(
            LoginAccessLog.failure(
                account,
                attemptedLoginId,
                reason,
                ipAddress,
                userAgent,
                latestRecordHash()
            )
        );

        return newlyLocked;
    }

    @Transactional
    public void recordLogout(String sessionId) {
        loginAccessLogRepository
            .findFirstBySessionIdAndSuccessTrueAndLogoutAtIsNull(sessionId)
            .ifPresent(LoginAccessLog::logout);
    }

    /*
     * 해시체인의 다음 고리를 이어 붙이기 위해 가장 최근 기록의 recordHash를 가져온다.
     * 동시에 두 로그인 시도가 겹치면 같은 previousHash를 가리키는 두 기록이
     * 생길 수 있는데, 이는 실제 위·변조와 구분되는 정상적인 분기이므로
     * 검증 시 별도로 표시한다.
     */
    private String latestRecordHash() {
        return loginAccessLogRepository
            .findTopByOrderByIdDesc()
            .map(LoginAccessLog::getRecordHash)
            .orElse(null);
    }
}
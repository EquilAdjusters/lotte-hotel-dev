package com.example.backendlotte.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.entity.LoginAccessLog;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.repository.LoginAccessLogRepository;
import com.example.backendlotte.account.type.LoginFailureReason;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final AccountRepository accountRepository;
    private final LoginAccessLogRepository loginAccessLogRepository;

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
                sessionId
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
        boolean locked = false;

        if (accountId != null) {
            account = accountRepository.findById(accountId)
                .orElse(null);

            if (account != null) {
                locked = account.loginFailed(maxFailures);
            }
        }

        loginAccessLogRepository.save(
            LoginAccessLog.failure(
                account,
                attemptedLoginId,
                reason,
                ipAddress,
                userAgent
            )
        );

        return locked;
    }
}
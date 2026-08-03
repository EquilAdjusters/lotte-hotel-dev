package com.example.backendlotte.account.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.account.entity.LoginAccessLog;

public interface LoginAccessLogRepository
        extends JpaRepository<LoginAccessLog, Long> {

    List<LoginAccessLog> findAllByAccountIdOrderByLoginAtDesc(
        Long accountId
    );

    List<LoginAccessLog> findAllByAttemptedLoginIdOrderByLoginAtDesc(
        String attemptedLoginId
    );

    long countByAttemptedLoginIdAndSuccessFalseAndLoginAtAfter(
        String attemptedLoginId,
        LocalDateTime after
    );
}
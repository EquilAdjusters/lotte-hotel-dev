package com.example.backendlotte.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.type.AccountStatus;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByLoginId(String loginId);

    Optional<Account> findByLoginIdAndStatus(
        String loginId,
        AccountStatus status
    );

    boolean existsByLoginId(String loginId);
}
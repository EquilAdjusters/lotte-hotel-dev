package com.example.backendlotte.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.backendlotte.account.entity.Account;

public interface AccountRepository
        extends JpaRepository<Account, Long>,
                JpaSpecificationExecutor<Account> {

    Optional<Account> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
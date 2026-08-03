package com.example.backendlotte.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.account.entity.AccountHistory;

public interface AccountHistoryRepository
        extends JpaRepository<AccountHistory, Long> {

    List<AccountHistory> findAllByAccountIdOrderByCreatedAtDesc(
        Long accountId
    );

    List<AccountHistory> findAllByActorAccountIdOrderByCreatedAtDesc(
        Long actorAccountId
    );
}
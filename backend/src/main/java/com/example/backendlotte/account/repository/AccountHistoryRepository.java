package com.example.backendlotte.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backendlotte.account.entity.AccountHistory;

public interface AccountHistoryRepository
        extends JpaRepository<AccountHistory, Long> {

    List<AccountHistory> findAllByAccountIdOrderByCreatedAtDesc(
        Long accountId
    );

    List<AccountHistory> findAllByActorAccountIdOrderByCreatedAtDesc(
        Long actorAccountId
    );


    Page<AccountHistory> findByAccountId(
        Long accountId,
        Pageable pageable
    );
}
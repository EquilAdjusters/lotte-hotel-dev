package com.example.backendlotte.claim.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.claim.entity.ClaimHistory;

public interface ClaimHistoryRepository
        extends JpaRepository<ClaimHistory, Long> {

    Page<ClaimHistory> findByClaimId(
        Long claimId,
        Pageable pageable
    );
}
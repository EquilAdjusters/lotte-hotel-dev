package com.example.backendlotte.claim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.claim.entity.ClaimConsent;

public interface ClaimConsentRepository
        extends JpaRepository<ClaimConsent, Long> {

    Optional<ClaimConsent> findByClaimId(
        Long claimId
    );

    boolean existsByClaimId(
        Long claimId
    );
}
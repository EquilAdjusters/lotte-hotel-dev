package com.example.backendlotte.claim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backendlotte.claim.entity.ClaimNumberSequence;

import jakarta.persistence.LockModeType;

public interface ClaimNumberSequenceRepository
        extends JpaRepository<ClaimNumberSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sequence
        FROM ClaimNumberSequence sequence
        WHERE sequence.period = :period
        """)
    Optional<ClaimNumberSequence> findByPeriodForUpdate(
        @Param("period") String period
    );
}
package com.example.backendlotte.claim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.claim.entity.ClaimViewLog;

public interface ClaimViewLogRepository
extends JpaRepository<ClaimViewLog, Long> {
}

package com.example.backendlotte.claim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.claim.entity.ClaimExportLog;

public interface ClaimExportLogRepository
extends JpaRepository<ClaimExportLog, Long> {
}
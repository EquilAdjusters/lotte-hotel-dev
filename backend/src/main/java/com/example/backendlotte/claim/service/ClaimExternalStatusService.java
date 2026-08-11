package com.example.backendlotte.claim.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimHistory;
import com.example.backendlotte.claim.repository.ClaimHistoryRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimExternalStatusService {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    @Transactional
    public void closeClaim(
            Long claimId,
            ClaimClosingResult closingResult
    ) {
        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        ClaimStatus previousStatus =
            claim.getStatus();

        claim.close(
            closingResult,
            LocalDateTime.now(
                ZoneId.of("Asia/Seoul")
            )
        );

        claimHistoryRepository.save(
            ClaimHistory.closedByExternalAdapter(
                claim,
                previousStatus,
                closingResult,
                buildClosingDescription(
                    closingResult
                )
            )
        );
    }

    private String buildClosingDescription(
            ClaimClosingResult closingResult
    ) {
        return switch (closingResult) {

            case INSURANCE_PAID ->
                "손해사정업체 연동 결과 종결(보험금 지급) 처리";

            case EXEMPTED ->
                "손해사정업체 연동 결과 종결(면책) 처리";
        };
    }
}
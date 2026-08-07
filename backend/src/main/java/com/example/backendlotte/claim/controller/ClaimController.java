package com.example.backendlotte.claim.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import com.example.backendlotte.claim.dto.ClaimHistoryResponse;
import com.example.backendlotte.auth.security.CustomUserDetails;
import com.example.backendlotte.claim.dto.ClaimCreateRequest;
import com.example.backendlotte.claim.dto.ClaimDuplicateResponse;
import com.example.backendlotte.claim.dto.ClaimResponse;
import com.example.backendlotte.claim.dto.ClaimUpdateRequest;
import com.example.backendlotte.claim.service.ClaimService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    /**
     * 사고접수
     *
     * 현재 접수 화면은 지점 공유계정 전용이다.
     * Service의 ClaimAccessContextResolver에서도 동일하게 재검증한다.
     */
    @PostMapping
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody ClaimCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ClaimResponse response =
            claimService.createClaim(
                request,
                user.getAccountId()
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    /**
     * 피해자명 + 생년월일 기준 중복 의심 접수 조회
     *
     * 결과가 있어도 신규 접수를 차단하지 않는다.
     */
    @GetMapping("/duplicates")
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public List<ClaimDuplicateResponse> findDuplicates(
            @RequestParam String victimName,
            @RequestParam LocalDate victimBirthDate
    ) {
        return claimService.findDuplicates(
                victimName,
                victimBirthDate);
    }
    
    @GetMapping("/{claimId}")
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public ClaimResponse findOne(
            @PathVariable Long claimId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return claimService.findOne(
                claimId,
                user.getAccountId());
    }
    
    @PatchMapping("/{claimId}")
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public ClaimResponse updateClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return claimService.updateClaim(
                claimId,
                request,
                user.getAccountId());
    }
    
    @GetMapping("/{claimId}/histories")
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public Page<ClaimHistoryResponse> findHistories(
            @PathVariable Long claimId,

            @PageableDefault(
                size = 20,
                sort = "createdAt",
                direction = Sort.Direction.DESC
            )
            Pageable pageable,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        return claimService.findHistories(
            claimId,
            user.getAccountId(),
            pageable
        );
    }
}
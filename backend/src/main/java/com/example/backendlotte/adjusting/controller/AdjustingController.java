package com.example.backendlotte.adjusting.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.backendlotte.adjusting.dto.AdjusterResponse;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyResponse;
import com.example.backendlotte.adjusting.dto.ClaimAssignRequest;
import com.example.backendlotte.adjusting.dto.ClaimAssignmentListResponse;
import com.example.backendlotte.adjusting.dto.ClaimAssignmentSearchCondition;
import com.example.backendlotte.adjusting.service.AdjustingService;
import com.example.backendlotte.auth.security.CustomUserDetails;
import com.example.backendlotte.adjusting.dto.AdjusterCreateRequest;
import com.example.backendlotte.adjusting.dto.AdjusterUpdateRequest;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyCreateRequest;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adjusting")
@RequiredArgsConstructor
public class AdjustingController {

    private final AdjustingService adjustingService;

    @GetMapping("/companies")
    @PreAuthorize(
        "hasAnyRole('ADMIN1', 'ADMIN2')"
    )
    public List<AdjustingCompanyResponse> findCompanies() {
        return adjustingService.findActiveCompanies();
    }

    @GetMapping("/companies/{companyId}/adjusters")
    @PreAuthorize(
        "hasAnyRole('ADMIN1', 'ADMIN2')"
    )
    public List<AdjusterResponse> findAdjusters(
            @PathVariable Long companyId
    ) {
        return adjustingService.findActiveAdjusters(
            companyId
        );
    }

    @PostMapping("/claims/{claimId}/assign")
    @PreAuthorize(
        "hasAnyRole('ADMIN1', 'ADMIN2')"
    )
    public ResponseEntity<Void> assignClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimAssignRequest request,
            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        adjustingService.assignClaim(
                claimId,
                request,
                user.getAccountId());

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/claims")
    @PreAuthorize(
        "hasAnyRole('ADMIN1', 'ADMIN2')"
    )
    public Page<ClaimAssignmentListResponse> findClaims(
            ClaimAssignmentSearchCondition condition,

            @PageableDefault(
                size = 10,
                sort = "createdAt",
                direction = Sort.Direction.DESC
            )
            Pageable pageable,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        return adjustingService.findClaims(
                condition,
                user.getAccountId(),
                pageable);
    }
    
    @PostMapping("/companies")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<AdjustingCompanyResponse> createCompany(
            @Valid @RequestBody
            AdjustingCompanyCreateRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                adjustingService.createCompany(request)
            );
    }

    @PatchMapping("/companies/{companyId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public AdjustingCompanyResponse updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody
            AdjustingCompanyUpdateRequest request
    ) {
        return adjustingService.updateCompany(
            companyId,
            request
        );
    }

    @PostMapping("/companies/{companyId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> deactivateCompany(
            @PathVariable Long companyId
    ) {
        adjustingService.deactivateCompany(companyId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/companies/{companyId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> activateCompany(
            @PathVariable Long companyId
    ) {
        adjustingService.activateCompany(companyId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/adjusters")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<AdjusterResponse> createAdjuster(
            @Valid @RequestBody
            AdjusterCreateRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                adjustingService.createAdjuster(request)
            );
    }

    @PatchMapping("/adjusters/{adjusterId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public AdjusterResponse updateAdjuster(
            @PathVariable Long adjusterId,
            @Valid @RequestBody
            AdjusterUpdateRequest request
    ) {
        return adjustingService.updateAdjuster(
            adjusterId,
            request
        );
    }

    @PostMapping("/adjusters/{adjusterId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> deactivateAdjuster(
            @PathVariable Long adjusterId
    ) {
        adjustingService.deactivateAdjuster(adjusterId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/adjusters/{adjusterId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> activateAdjuster(
            @PathVariable Long adjusterId
    ) {
        adjustingService.activateAdjuster(adjusterId);

        return ResponseEntity.noContent().build();
    }
}
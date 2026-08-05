package com.example.backendlotte.organization.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.organization.dto.BranchCreateRequest;
import com.example.backendlotte.organization.dto.BranchResponse;
import com.example.backendlotte.organization.dto.BranchUpdateRequest;
import com.example.backendlotte.organization.service.BranchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<BranchResponse> create(
            @RequestBody BranchCreateRequest request
    ) {
        BranchResponse response = branchService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public List<BranchResponse> findAll(
            @RequestParam(required = false)
            Long hotelId,

            @RequestParam(defaultValue = "true")
            boolean activeOnly
    ) {
        return branchService.findAll(
            hotelId,
            activeOnly
        );
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public BranchResponse findOne(
            @PathVariable Long branchId
    ) {
        return branchService.findOne(branchId);
    }

    @PatchMapping("/{branchId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public BranchResponse update(
            @PathVariable Long branchId,
            @RequestBody BranchUpdateRequest request
    ) {
        return branchService.update(
            branchId,
            request
        );
    }

    @DeleteMapping("/{branchId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> deactivate(
            @PathVariable Long branchId
    ) {
        branchService.deactivate(branchId);

        return ResponseEntity.ok(
            Map.of(
                "code", "BRANCH_DEACTIVATED",
                "message", "지점이 사용 중지되었습니다."
            )
        );
    }

    @PatchMapping("/{branchId}/activate")
    @PreAuthorize("hasRole('ADMIN1')")
    public BranchResponse activate(
            @PathVariable Long branchId
    ) {
        return branchService.activate(branchId);
    }
}
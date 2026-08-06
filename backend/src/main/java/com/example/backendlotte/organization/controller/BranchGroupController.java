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

import com.example.backendlotte.organization.dto.BranchGroupCreateRequest;
import com.example.backendlotte.organization.dto.BranchGroupResponse;
import com.example.backendlotte.organization.dto.BranchGroupUpdateRequest;
import com.example.backendlotte.organization.service.BranchGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branch-groups")
@RequiredArgsConstructor
public class BranchGroupController {

    private final BranchGroupService branchGroupService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<BranchGroupResponse> create(
            @RequestBody BranchGroupCreateRequest request
    ) {
        BranchGroupResponse response =
            branchGroupService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public List<BranchGroupResponse> findAll(
            @RequestParam(defaultValue = "true")
            boolean activeOnly
    ) {
        return branchGroupService.findAll(activeOnly);
    }

    @GetMapping("/{branchGroupId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public BranchGroupResponse findOne(
            @PathVariable Long branchGroupId
    ) {
        return branchGroupService.findOne(branchGroupId);
    }

    @PatchMapping("/{branchGroupId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public BranchGroupResponse update(
            @PathVariable Long branchGroupId,
            @RequestBody BranchGroupUpdateRequest request
    ) {
        return branchGroupService.update(
            branchGroupId,
            request
        );
    }

    @DeleteMapping("/{branchGroupId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> deactivate(
            @PathVariable Long branchGroupId
    ) {
        branchGroupService.deactivate(branchGroupId);

        return ResponseEntity.ok(
            Map.of(
                "code", "BRANCH_GROUP_DEACTIVATED",
                "message", "권역 그룹이 사용 중지되었습니다."
            )
        );
    }

    @PatchMapping("/{branchGroupId}/activate")
    @PreAuthorize("hasRole('ADMIN1')")
    public BranchGroupResponse activate(
            @PathVariable Long branchGroupId
    ) {
        return branchGroupService.activate(branchGroupId);
    }
    
}
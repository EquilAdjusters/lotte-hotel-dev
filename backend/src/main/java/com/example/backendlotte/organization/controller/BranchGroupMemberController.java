package com.example.backendlotte.organization.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.organization.dto.BranchGroupMemberCreateRequest;
import com.example.backendlotte.organization.dto.BranchGroupMemberResponse;
import com.example.backendlotte.organization.service.BranchGroupMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branch-group-members")
@RequiredArgsConstructor
public class BranchGroupMemberController {

    private final BranchGroupMemberService branchGroupMemberService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<BranchGroupMemberResponse> create(
            @RequestBody BranchGroupMemberCreateRequest request
    ) {
        BranchGroupMemberResponse response =
            branchGroupMemberService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/branch-groups/{branchGroupId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public List<BranchGroupMemberResponse> findAllByBranchGroup(
            @PathVariable Long branchGroupId
    ) {
        return branchGroupMemberService
            .findAllByBranchGroup(branchGroupId);
    }

    @GetMapping("/branches/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3', 'ADMIN4')")
    public List<BranchGroupMemberResponse> findAllByBranch(
            @PathVariable Long branchId
    ) {
        return branchGroupMemberService
            .findAllByBranch(branchId);
    }

    @DeleteMapping("/branch-groups/{branchGroupId}/branches/{branchId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long branchGroupId,
            @PathVariable Long branchId
    ) {
        branchGroupMemberService.delete(
            branchGroupId,
            branchId
        );

        return ResponseEntity.ok(
            Map.of(
                "code", "BRANCH_GROUP_MEMBER_DELETED",
                "message", "권역 그룹과 지점의 연결이 해제되었습니다."
            )
        );
    }
}
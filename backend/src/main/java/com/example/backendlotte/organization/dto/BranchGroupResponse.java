package com.example.backendlotte.organization.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.organization.entity.BranchGroup;

public record BranchGroupResponse(
    Long id,
    String name,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static BranchGroupResponse from(
            BranchGroup branchGroup
    ) {
        return new BranchGroupResponse(
            branchGroup.getId(),
            branchGroup.getName(),
            branchGroup.isActive(),
            branchGroup.getCreatedAt(),
            branchGroup.getUpdatedAt()
        );
    }
}
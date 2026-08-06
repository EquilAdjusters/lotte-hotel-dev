package com.example.backendlotte.organization.dto;

public record BranchGroupMemberCreateRequest(

    Long branchGroupId,

    Long branchId

) {
}
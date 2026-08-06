package com.example.backendlotte.organization.dto;

import com.example.backendlotte.organization.entity.BranchGroupMember;

public record BranchGroupMemberResponse(

    Long id,

    Long branchGroupId,

    String branchGroupName,

    Long branchId,

    String branchName

) {

    public static BranchGroupMemberResponse from(
            BranchGroupMember member
    ) {
        return new BranchGroupMemberResponse(
            member.getId(),
            member.getBranchGroup().getId(),
            member.getBranchGroup().getName(),
            member.getBranch().getId(),
            member.getBranch().getName()
        );
    }
}
package com.example.backendlotte.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.BranchGroupMember;

public interface BranchGroupMemberRepository
        extends JpaRepository<BranchGroupMember, Long> {

    List<BranchGroupMember>
        findAllByBranchGroupId(Long branchGroupId);

    List<BranchGroupMember>
        findAllByBranchId(Long branchId);

    boolean existsByBranchGroupIdAndBranchId(
        Long branchGroupId,
        Long branchId
    );

    Optional<BranchGroupMember>
        findByBranchGroupIdAndBranchId(
            Long branchGroupId,
            Long branchId
        );

    void deleteByBranchGroupIdAndBranchId(
        Long branchGroupId,
        Long branchId
    );
}
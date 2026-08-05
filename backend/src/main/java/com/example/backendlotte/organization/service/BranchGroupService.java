package com.example.backendlotte.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.organization.dto.BranchGroupCreateRequest;
import com.example.backendlotte.organization.dto.BranchGroupResponse;
import com.example.backendlotte.organization.dto.BranchGroupUpdateRequest;
import com.example.backendlotte.organization.entity.BranchGroup;
import com.example.backendlotte.organization.repository.BranchGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchGroupService {

    private final BranchGroupRepository branchGroupRepository;

    @Transactional
    public BranchGroupResponse create(
            BranchGroupCreateRequest request
    ) {
        String name = normalizeName(request.name());

        if (branchGroupRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "이미 등록된 권역 그룹명입니다."
            );
        }

        BranchGroup branchGroup =
            BranchGroup.create(name);

        branchGroupRepository.save(branchGroup);

        return BranchGroupResponse.from(branchGroup);
    }

    @Transactional(readOnly = true)
    public List<BranchGroupResponse> findAll(
            boolean activeOnly
    ) {
        List<BranchGroup> branchGroups = activeOnly
            ? branchGroupRepository
                .findAllByActiveTrueOrderByNameAsc()
            : branchGroupRepository
                .findAllByOrderByNameAsc();

        return branchGroups.stream()
            .map(BranchGroupResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public BranchGroupResponse findOne(
            Long branchGroupId
    ) {
        return BranchGroupResponse.from(
            findById(branchGroupId)
        );
    }

    @Transactional
    public BranchGroupResponse update(
            Long branchGroupId,
            BranchGroupUpdateRequest request
    ) {
        BranchGroup branchGroup =
            findById(branchGroupId);

        String name = normalizeName(request.name());

        if (branchGroupRepository.existsByNameAndIdNot(
                name,
                branchGroupId
        )) {
            throw new IllegalArgumentException(
                "이미 등록된 권역 그룹명입니다."
            );
        }

        branchGroup.updateName(name);

        return BranchGroupResponse.from(branchGroup);
    }

    @Transactional
    public void deactivate(
            Long branchGroupId
    ) {
        BranchGroup branchGroup =
            findById(branchGroupId);

        branchGroup.deactivate();
    }

    @Transactional
    public BranchGroupResponse activate(
            Long branchGroupId
    ) {
        BranchGroup branchGroup =
            findById(branchGroupId);

        branchGroup.activate();

        return BranchGroupResponse.from(branchGroup);
    }

    private BranchGroup findById(
            Long branchGroupId
    ) {
        return branchGroupRepository
            .findById(branchGroupId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "권역 그룹을 찾을 수 없습니다."
                )
            );
    }

    private String normalizeName(
            String name
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "권역 그룹명은 필수입니다."
            );
        }

        String normalized = name.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                "권역 그룹명은 100자 이하로 입력해주세요."
            );
        }

        return normalized;
    }
}
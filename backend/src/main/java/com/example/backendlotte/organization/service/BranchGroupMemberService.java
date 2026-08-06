package com.example.backendlotte.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.organization.dto.BranchGroupMemberCreateRequest;
import com.example.backendlotte.organization.dto.BranchGroupMemberResponse;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.BranchGroup;
import com.example.backendlotte.organization.entity.BranchGroupMember;
import com.example.backendlotte.organization.repository.BranchGroupMemberRepository;
import com.example.backendlotte.organization.repository.BranchGroupRepository;
import com.example.backendlotte.organization.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchGroupMemberService {

    private final BranchGroupMemberRepository branchGroupMemberRepository;
    private final BranchGroupRepository branchGroupRepository;
    private final BranchRepository branchRepository;

    /**
     * 권역 그룹에 지점을 연결한다.
     */
    @Transactional
    public BranchGroupMemberResponse create(
            BranchGroupMemberCreateRequest request
    ) {
        BranchGroup branchGroup =
            findActiveBranchGroup(request.branchGroupId());

        Branch branch =
            findActiveBranch(request.branchId());

        boolean alreadyExists =
            branchGroupMemberRepository
                .existsByBranchGroupIdAndBranchId(
                    branchGroup.getId(),
                    branch.getId()
                );

        if (alreadyExists) {
            throw new IllegalArgumentException(
                "이미 해당 권역 그룹에 등록된 지점입니다."
            );
        }

        BranchGroupMember member =
            BranchGroupMember.create(
                branchGroup,
                branch
            );

        branchGroupMemberRepository.save(member);

        return BranchGroupMemberResponse.from(member);
    }

    /**
     * 특정 권역 그룹에 연결된 지점 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<BranchGroupMemberResponse> findAllByBranchGroup(
            Long branchGroupId
    ) {
        // 존재하지 않는 그룹 ID로 빈 목록이 반환되는 것을 방지
        findBranchGroup(branchGroupId);

        return branchGroupMemberRepository
            .findAllByBranchGroupId(branchGroupId)
            .stream()
            .map(BranchGroupMemberResponse::from)
            .toList();
    }

    /**
     * 특정 지점이 속한 권역 그룹 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<BranchGroupMemberResponse> findAllByBranch(
            Long branchId
    ) {
        // 존재하지 않는 지점 ID로 빈 목록이 반환되는 것을 방지
        findBranch(branchId);

        return branchGroupMemberRepository
            .findAllByBranchId(branchId)
            .stream()
            .map(BranchGroupMemberResponse::from)
            .toList();
    }

    /**
     * 특정 권역 그룹과 지점의 연결을 해제한다.
     */
    @Transactional
    public void delete(
            Long branchGroupId,
            Long branchId
    ) {
        BranchGroupMember member =
            branchGroupMemberRepository
                .findByBranchGroupIdAndBranchId(
                    branchGroupId,
                    branchId
                )
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "권역 그룹과 지점의 연결 정보를 찾을 수 없습니다."
                    )
                );

        branchGroupMemberRepository.delete(member);
    }

    private BranchGroup findActiveBranchGroup(
            Long branchGroupId
    ) {
        BranchGroup branchGroup =
            findBranchGroup(branchGroupId);

        if (!branchGroup.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 권역 그룹에는 지점을 등록할 수 없습니다."
            );
        }

        return branchGroup;
    }

    private Branch findActiveBranch(
            Long branchId
    ) {
        Branch branch =
            findBranch(branchId);

        if (!branch.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 지점은 권역 그룹에 등록할 수 없습니다."
            );
        }

        if (branch.getHotel() == null
                || !branch.getHotel().isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔에 속한 지점은 등록할 수 없습니다."
            );
        }

        if (branch.getHotel().getHotelCompany() == null
                || !branch.getHotel()
                    .getHotelCompany()
                    .isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔사에 속한 지점은 등록할 수 없습니다."
            );
        }

        return branch;
    }

    private BranchGroup findBranchGroup(
            Long branchGroupId
    ) {
        if (branchGroupId == null) {
            throw new IllegalArgumentException(
                "권역 그룹 ID는 필수입니다."
            );
        }

        return branchGroupRepository
            .findById(branchGroupId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "권역 그룹을 찾을 수 없습니다."
                )
            );
    }

    private Branch findBranch(
            Long branchId
    ) {
        if (branchId == null) {
            throw new IllegalArgumentException(
                "지점 ID는 필수입니다."
            );
        }

        return branchRepository
            .findById(branchId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "지점을 찾을 수 없습니다."
                )
            );
    }
}
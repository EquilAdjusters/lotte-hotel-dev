package com.example.backendlotte.account.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.dto.AccountCreateRequest;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.hotel.repository.HotelRepository;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.BranchGroup;
import com.example.backendlotte.organization.entity.HotelCompany;
import com.example.backendlotte.organization.repository.BranchGroupRepository;
import com.example.backendlotte.organization.repository.BranchRepository;
import com.example.backendlotte.organization.repository.HotelCompanyRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountAffiliationResolver {

    private final HotelCompanyRepository hotelCompanyRepository;
    private final HotelRepository hotelRepository;
    private final BranchRepository branchRepository;
    private final BranchGroupRepository branchGroupRepository;

    @Transactional(readOnly = true)
    public ResolvedAffiliation resolve(
            AccountCreateRequest request
    ) {
        if (request.role() == null) {
            throw new IllegalArgumentException(
                "역할은 필수입니다."
            );
        }

        return switch (request.role()) {
            case ADMIN1 -> resolveAdmin1(request);
            case ADMIN2 -> resolveAdmin2(request);
            case ADMIN3 -> resolveAdmin3(request);
            case BRANCH_SHARED -> resolveBranchShared(request);
            case ADMIN4 -> resolveAdmin4(request);
        };
    }

    private ResolvedAffiliation resolveAdmin1(
            AccountCreateRequest request
    ) {
        requireScopeType(request.scopeType(), ScopeType.ALL);
        requireSharedAccount(request.sharedAccount(), false);
        requireAllAffiliationIdsNull(request);

        return ResolvedAffiliation.empty();
    }

    private ResolvedAffiliation resolveAdmin2(
            AccountCreateRequest request
    ) {
        /*
         * ADMIN2는 현재 전체 사고를 배정·확인하는 역할이므로
         * 특정 호텔·지점·권역 소속을 연결하지 않는다.
         *
         * scopeType은 회의 결과에 따라 ALL 또는 ASSIGNED가 될 수 있어
         * 여기서는 두 값 모두 허용한다.
         */
        if (request.scopeType() != ScopeType.ALL
                && request.scopeType() != ScopeType.ASSIGNED) {
            throw new IllegalArgumentException(
                "ADMIN2의 조회 범위는 ALL 또는 ASSIGNED여야 합니다."
            );
        }

        requireSharedAccount(request.sharedAccount(), false);
        requireAllAffiliationIdsNull(request);

        return ResolvedAffiliation.empty();
    }

    private ResolvedAffiliation resolveAdmin3(
            AccountCreateRequest request
    ) {
        requireScopeType(request.scopeType(), ScopeType.HOTEL);
        requireSharedAccount(request.sharedAccount(), false);

        if (request.hotelCompanyId() == null) {
            throw new IllegalArgumentException(
                "ADMIN3 계정은 호텔사 ID가 필수입니다."
            );
        }

        if (request.hotelId() != null
                || request.branchId() != null
                || request.branchGroupId() != null) {
            throw new IllegalArgumentException(
                "ADMIN3 계정에는 호텔사 외의 소속을 지정할 수 없습니다."
            );
        }

        HotelCompany hotelCompany =
            findActiveHotelCompany(request.hotelCompanyId());

        return new ResolvedAffiliation(
            hotelCompany,
            null,
            null,
            null
        );
    }

    private ResolvedAffiliation resolveBranchShared(
            AccountCreateRequest request
    ) {
        requireScopeType(request.scopeType(), ScopeType.BRANCH);
        requireSharedAccount(request.sharedAccount(), true);

        if (request.branchId() == null) {
            throw new IllegalArgumentException(
                "지점 공유계정은 지점 ID가 필수입니다."
            );
        }

        if (request.hotelCompanyId() != null
                || request.hotelId() != null
                || request.branchGroupId() != null) {
            throw new IllegalArgumentException(
                "지점 공유계정은 branchId만 지정해야 합니다."
            );
        }

        Branch branch = findActiveBranch(request.branchId());
        Hotel hotel = branch.getHotel();
        HotelCompany hotelCompany = hotel.getHotelCompany();

        /*
         * 지점 계정은 branch만 지정받지만,
         * 조회와 권한 검사를 편하게 하기 위해 상위 소속도 함께 저장한다.
         */
        return new ResolvedAffiliation(
            hotelCompany,
            hotel,
            branch,
            null
        );
    }

    private ResolvedAffiliation resolveAdmin4(
            AccountCreateRequest request
    ) {
        requireScopeType(
            request.scopeType(),
            ScopeType.BRANCH_GROUP
        );

        /*
         * 현재 설계서상 ADMIN4도 권역팀 공동 사용 성격이며
         * 동시접속이 허용되는 계정으로 관리한다.
         */
        requireSharedAccount(request.sharedAccount(), true);

        if (request.branchGroupId() == null) {
            throw new IllegalArgumentException(
                "ADMIN4 계정은 권역 그룹 ID가 필수입니다."
            );
        }

        if (request.hotelCompanyId() != null
                || request.hotelId() != null
                || request.branchId() != null) {
            throw new IllegalArgumentException(
                "ADMIN4 계정에는 권역 그룹만 지정할 수 있습니다."
            );
        }

        BranchGroup branchGroup =
            findActiveBranchGroup(request.branchGroupId());

        return new ResolvedAffiliation(
            null,
            null,
            null,
            branchGroup
        );
    }

    private HotelCompany findActiveHotelCompany(
            Long hotelCompanyId
    ) {
        HotelCompany hotelCompany =
            hotelCompanyRepository
                .findById(hotelCompanyId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "호텔사를 찾을 수 없습니다."
                    )
                );

        if (!hotelCompany.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔사는 계정 소속으로 지정할 수 없습니다."
            );
        }

        return hotelCompany;
    }

    private Hotel findActiveHotel(Long hotelId) {
        Hotel hotel = hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "호텔을 찾을 수 없습니다."
                )
            );

        if (!hotel.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔은 계정 소속으로 지정할 수 없습니다."
            );
        }

        if (hotel.getHotelCompany() == null
                || !hotel.getHotelCompany().isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔사에 속한 호텔은 지정할 수 없습니다."
            );
        }

        return hotel;
    }

    private Branch findActiveBranch(Long branchId) {
        Branch branch = branchRepository
            .findById(branchId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "지점을 찾을 수 없습니다."
                )
            );

        if (!branch.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 지점은 계정 소속으로 지정할 수 없습니다."
            );
        }

        Hotel hotel = findActiveHotel(
            branch.getHotel().getId()
        );

        if (!hotel.getId().equals(branch.getHotel().getId())) {
            throw new IllegalArgumentException(
                "지점의 호텔 소속 정보가 올바르지 않습니다."
            );
        }

        return branch;
    }

    private BranchGroup findActiveBranchGroup(
            Long branchGroupId
    ) {
        BranchGroup branchGroup =
            branchGroupRepository
                .findById(branchGroupId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "권역 그룹을 찾을 수 없습니다."
                    )
                );

        if (!branchGroup.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 권역 그룹은 계정 소속으로 지정할 수 없습니다."
            );
        }

        return branchGroup;
    }

    private void requireScopeType(
            ScopeType actual,
            ScopeType expected
    ) {
        if (actual == null) {
            throw new IllegalArgumentException(
                "조회 범위는 필수입니다."
            );
        }

        if (actual != expected) {
            throw new IllegalArgumentException(
                "해당 역할의 조회 범위는 "
                    + expected
                    + "여야 합니다."
            );
        }
    }

    private void requireSharedAccount(
            boolean actual,
            boolean expected
    ) {
        if (actual != expected) {
            if (expected) {
                throw new IllegalArgumentException(
                    "해당 역할은 공유계정으로 생성해야 합니다."
                );
            }

            throw new IllegalArgumentException(
                "해당 역할은 공유계정으로 생성할 수 없습니다."
            );
        }
    }

    private void requireAllAffiliationIdsNull(
            AccountCreateRequest request
    ) {
        if (request.hotelCompanyId() != null
                || request.hotelId() != null
                || request.branchId() != null
                || request.branchGroupId() != null) {
            throw new IllegalArgumentException(
                "해당 역할에는 조직 소속을 지정할 수 없습니다."
            );
        }
    }

    public record ResolvedAffiliation(
        HotelCompany hotelCompany,
        Hotel hotel,
        Branch branch,
        BranchGroup branchGroup
    ) {

        public static ResolvedAffiliation empty() {
            return new ResolvedAffiliation(
                null,
                null,
                null,
                null
            );
        }
    }
}
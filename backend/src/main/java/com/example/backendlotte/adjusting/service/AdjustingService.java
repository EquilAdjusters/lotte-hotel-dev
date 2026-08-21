package com.example.backendlotte.adjusting.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.adjusting.dto.AdjusterCreateRequest;
import com.example.backendlotte.adjusting.dto.AdjusterResponse;
import com.example.backendlotte.adjusting.dto.AdjusterUpdateRequest;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyCreateRequest;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyResponse;
import com.example.backendlotte.adjusting.dto.AdjustingCompanyUpdateRequest;
import com.example.backendlotte.adjusting.dto.ClaimAssignRequest;
import com.example.backendlotte.adjusting.dto.ClaimAssignmentListResponse;
import com.example.backendlotte.adjusting.dto.ClaimAssignmentSearchCondition;
import com.example.backendlotte.adjusting.entity.Adjuster;
import com.example.backendlotte.adjusting.entity.AdjustingCompany;
import com.example.backendlotte.adjusting.repository.AdjusterRepository;
import com.example.backendlotte.adjusting.repository.AdjustingCompanyRepository;
import com.example.backendlotte.organization.entity.HotelCompany;
import com.example.backendlotte.organization.repository.HotelCompanyRepository;
import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimHistory;
import com.example.backendlotte.claim.event.ClaimAssignedEvent;
import com.example.backendlotte.claim.repository.ClaimHistoryRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.specification.ClaimSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustingService {

    private final AdjustingCompanyRepository adjustingCompanyRepository;
    private final AdjusterRepository adjusterRepository;
    private final ClaimRepository claimRepository;
    private final AccountRepository accountRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final HotelCompanyRepository hotelCompanyRepository;

    @Transactional(readOnly = true)
    public List<AdjustingCompanyResponse> findCompanies(
            boolean activeOnly
    ) {
        List<AdjustingCompany> companies = activeOnly
            ? adjustingCompanyRepository.findAllByActiveTrueOrderByNameAsc()
            : adjustingCompanyRepository.findAllByOrderByNameAsc();

        return companies
            .stream()
            .map(AdjustingCompanyResponse::from)
            .toList();
    }

    /*
     * AD-02 손사업체 지정 드롭다운용.
     * AD-01에서 그 접수건의 호텔사에 연결해 둔 손사업체만 노출한다.
     */
    @Transactional(readOnly = true)
    public List<AdjustingCompanyResponse> findCompaniesForClaim(
            Long claimId
    ) {
        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."));

        return adjustingCompanyRepository
            .findAllByHotelCompanies_IdAndActiveTrueOrderByNameAsc(
                claim.getHotelCompany().getId())
            .stream()
            .map(AdjustingCompanyResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AdjusterResponse> findAdjusters(
            Long adjustingCompanyId,
            boolean activeOnly
    ) {
        List<Adjuster> adjusters = activeOnly
            ? adjusterRepository.findAllByAdjustingCompanyIdAndActiveTrueOrderByNameAsc(
                adjustingCompanyId)
            : adjusterRepository.findAllByAdjustingCompanyIdOrderByNameAsc(
                adjustingCompanyId);

        return adjusters
            .stream()
            .map(AdjusterResponse::from)
            .toList();
    }

    @Transactional
    public void assignClaim(
            Long claimId,
            ClaimAssignRequest request,
            Long accountId
    ) {
        Account actor = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "작업 계정을 찾을 수 없습니다."));

        if (actor.getRole() != Role.ADMIN1
                && actor.getRole() != Role.ADMIN2) {
            throw new AccessDeniedException(
                    "손해사정 배정 권한이 없습니다.");
        }

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "접수건을 찾을 수 없습니다."));

        AdjustingCompany company = adjustingCompanyRepository
                .findById(request.adjustingCompanyId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "손해사정업체를 찾을 수 없습니다."));

        Adjuster adjuster = adjusterRepository
                .findById(request.adjusterId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "손해사정 담당자를 찾을 수 없습니다."));

        if (!company.isActive()) {
            throw new IllegalStateException(
                    "사용 중지된 손해사정업체입니다.");
        }

        if (!adjuster.isActive()) {
            throw new IllegalStateException(
                    "사용 중지된 손해사정 담당자입니다.");
        }

        boolean firstAssignment = claim.getAdjustingCompany() == null
                && claim.getAdjuster() == null;

        String previousValue = buildAssignmentSnapshot(claim);

        // 실제 배정을 먼저 수행
        claim.assignAdjuster(
                company,
                adjuster,
                actor,
                LocalDateTime.now(
                        ZoneId.of("Asia/Seoul")));

        // 변경된 이후 값
        String currentValue = buildAssignmentSnapshot(claim);

        if (firstAssignment) {

            claimHistoryRepository.save(
                    ClaimHistory.assigned(
                            claim,
                            actor,
                            currentValue));

        } else {

            if (previousValue.equals(currentValue)) {
                throw new IllegalArgumentException(
                        "기존 배정정보와 동일합니다.");
            }

            claimHistoryRepository.save(
                    ClaimHistory.assignmentChanged(
                            claim,
                            actor,
                            previousValue,
                            currentValue));
        }

        eventPublisher.publishEvent(
                new ClaimAssignedEvent(claimId));
    }
    
    private String buildAssignmentSnapshot(
        Claim claim
    ) {
            if (claim.getAdjustingCompany() == null
                            || claim.getAdjuster() == null) {
                    return """
                                    {
                                    "adjustingCompanyId": null,
                                    "adjustingCompanyName": null,
                                    "adjusterId": null,
                                    "adjusterName": null,
                                    "adjusterPhone": null
                                    }
                                    """.trim();
            }

            return """
                            {
                            "adjustingCompanyId": %d,
                            "adjustingCompanyName": "%s",
                            "adjusterId": %d,
                            "adjusterName": "%s",
                            "adjusterPhone": "%s"
                            }
                            """.formatted(
                            claim.getAdjustingCompany().getId(),
                            claim.getAdjustingCompany().getName(),
                            claim.getAdjuster().getId(),
                            claim.getAdjuster().getName(),
                            claim.getAdjuster().getPhone()).trim();
    }
    
    @Transactional(readOnly = true)
        public Page<ClaimAssignmentListResponse> findClaims(
                ClaimAssignmentSearchCondition condition,
                Long accountId,
                Pageable pageable
        ) {
                Account actor = accountRepository
                                .findById(accountId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "작업 계정을 찾을 수 없습니다."));

                if (actor.getRole() != Role.ADMIN1
                                && actor.getRole() != Role.ADMIN2) {
                        throw new AccessDeniedException(
                                        "손해사정 배정 관리 권한이 없습니다.");
                }

                Specification<Claim> spec = Specification.allOf(
                                ClaimSpecification.receivedBetween(
                                                condition.receivedFrom(),
                                                condition.receivedTo()),
                                ClaimSpecification.victimNameContains(
                                                condition.victimName()),
                                ClaimSpecification.assignmentStatusEquals(
                                                condition.assigned()));

                return claimRepository
                                .findAll(
                                                spec,
                                                pageable)
                                .map(
                                                ClaimAssignmentListResponse::from);
        }
        
        @Transactional
        public AdjustingCompanyResponse createCompany(
                AdjustingCompanyCreateRequest request
        ) {
        AdjustingCompany company =
                AdjustingCompany.create(
                request.name().trim(),
                trimToNull(request.businessNumber()),
                resolveHotelCompanies(request.hotelCompanyIds())
                );

        adjustingCompanyRepository.save(company);

        return AdjustingCompanyResponse.from(company);
        }

        @Transactional
        public AdjustingCompanyResponse updateCompany(
                Long companyId,
                AdjustingCompanyUpdateRequest request
        ) {
        AdjustingCompany company =
                adjustingCompanyRepository
                .findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정업체를 찾을 수 없습니다."
                        )
                );

        company.update(
                request.name().trim(),
                trimToNull(request.businessNumber())
        );

        company.updateHotelCompanies(
                resolveHotelCompanies(request.hotelCompanyIds())
        );

        return AdjustingCompanyResponse.from(company);
        }

        @Transactional
        public void deactivateCompany(
                Long companyId
        ) {
        AdjustingCompany company =
                adjustingCompanyRepository
                .findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정업체를 찾을 수 없습니다."
                        )
                );

        company.deactivate();
        }

        @Transactional
        public void activateCompany(
                Long companyId
        ) {
        AdjustingCompany company =
                adjustingCompanyRepository
                .findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정업체를 찾을 수 없습니다."
                        )
                );

        company.activate();
        }

        @Transactional
        public AdjusterResponse createAdjuster(
                AdjusterCreateRequest request
        ) {
        AdjustingCompany company =
                adjustingCompanyRepository
                .findById(request.adjustingCompanyId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정업체를 찾을 수 없습니다."
                        )
                );

        if (!company.isActive()) {
                throw new IllegalStateException(
                "사용 중지된 손해사정업체에는 담당자를 등록할 수 없습니다."
                );
        }

        Adjuster adjuster =
                Adjuster.create(
                company,
                request.name().trim(),
                request.phone().trim()
                );

        adjusterRepository.save(adjuster);

        return AdjusterResponse.from(adjuster);
        }

        @Transactional
        public AdjusterResponse updateAdjuster(
                Long adjusterId,
                AdjusterUpdateRequest request
        ) {
        Adjuster adjuster =
                adjusterRepository
                .findById(adjusterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정 담당자를 찾을 수 없습니다."
                        )
                );

        adjuster.update(
                request.name().trim(),
                request.phone().trim()
        );

        return AdjusterResponse.from(adjuster);
        }

        @Transactional
        public void deactivateAdjuster(
                Long adjusterId
        ) {
        Adjuster adjuster =
                adjusterRepository
                .findById(adjusterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정 담당자를 찾을 수 없습니다."
                        )
                );

        adjuster.deactivate();
        }

        @Transactional
        public void activateAdjuster(
                Long adjusterId
        ) {
        Adjuster adjuster =
                adjusterRepository
                .findById(adjusterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                        "손해사정 담당자를 찾을 수 없습니다."
                        )
                );

        adjuster.activate();
        }

        private Set<HotelCompany> resolveHotelCompanies(
                List<Long> hotelCompanyIds
        ) {
        if (hotelCompanyIds == null
                        || hotelCompanyIds.isEmpty()) {
                return new HashSet<>();
        }

        return new HashSet<>(
                hotelCompanyRepository.findAllById(hotelCompanyIds)
        );
        }

        private String trimToNull(
                String value
        ) {
        if (value == null) {
                return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
        }
}
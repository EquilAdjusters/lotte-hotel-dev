package com.example.backendlotte.claim.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.backendlotte.claim.dto.ClaimHistoryResponse;
import com.example.backendlotte.claim.dto.ClaimListResponse;
import com.example.backendlotte.claim.event.ClaimCreatedEvent;
import com.example.backendlotte.claim.type.PreferredLanguage;
import com.example.backendlotte.claim.dto.ClaimUpdateRequest;
import com.example.backendlotte.claim.type.ConsentStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.claim.dto.ClaimCreateRequest;
import com.example.backendlotte.claim.dto.ClaimDuplicateResponse;
import com.example.backendlotte.claim.dto.ClaimResponse;
import com.example.backendlotte.claim.dto.ClaimSearchCondition;
import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimConsent;
import com.example.backendlotte.claim.repository.ClaimConsentRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.specification.ClaimSpecification;
import com.example.backendlotte.claim.type.VictimType;
import com.example.backendlotte.organization.repository.BranchGroupMemberRepository;
import com.example.backendlotte.claim.entity.ClaimHistory;
import com.example.backendlotte.claim.repository.ClaimHistoryRepository;
import com.example.backendlotte.claim.type.ClaimStatus;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimConsentRepository claimConsentRepository;

    private final ClaimNumberGenerator claimNumberGenerator;
    private final ClaimAccessContextResolver claimAccessContextResolver;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BranchGroupMemberRepository branchGroupMemberRepository;

    private void validateSearchPeriod(
        ClaimSearchCondition condition
    ) {
        if (condition.receivedFrom() != null
                && condition.receivedTo() != null
                && condition.receivedFrom()
                    .isAfter(condition.receivedTo())) {

            throw new IllegalArgumentException(
                "접수일자 시작일은 종료일보다 늦을 수 없습니다."
            );
        }

        if (condition.accidentFrom() != null
                && condition.accidentTo() != null
                && condition.accidentFrom()
                    .isAfter(condition.accidentTo())) {

            throw new IllegalArgumentException(
                "사고일자 시작일은 종료일보다 늦을 수 없습니다."
            );
        }
    }

    /**
     * 사고접수
     */
    @Transactional
    public ClaimResponse createClaim(
            ClaimCreateRequest request,
            Long accountId
    ) {
        // 1. 로그인 계정 및 피해장소 확정
        ClaimAccessContext context =
            claimAccessContextResolver.resolveForCreate(
                accountId
            );

        // 2. 요청값 간 조합 검증
        validateCreateRequest(request);

        // 3. 시스템 접수번호 생성
        String claimNumber =
            claimNumberGenerator.generate();

        // 4. 사고접수 본체 생성
        Claim claim = Claim.create(
            claimNumber,

            context.hotelCompany(),
            context.hotel(),
            context.branch(),
            context.account(),

            request.victimName().trim(),
            request.victimPhone().trim(),
            request.victimBirthDate(),
            request.victimType(),
            request.preferredLanguage(),

            request.residenceSido().trim(),
            request.residenceSigungu().trim(),
            trimToNull(request.residenceDetail()),

            request.claimType(),
            request.accidentAt(),
            request.accidentDescription().trim(),

            request.receivedByName().trim(),
            request.receivedByExtension().trim()
        );

        claimRepository.save(claim);

        /*
        * 최초 접수 생성 이력
        *
        * 이 시점의 상태는 RECEIVED다.
        */
        claimHistoryRepository.save(
            ClaimHistory.created(
                claim,
                context.account()
            )
        );

        // 5. 개인정보 동의 증적 생성
        ClaimConsent consent =
            ClaimConsent.create(
                claim,
                request.consent().consentStatus(),
                request.consent().consentObtainedAt(),
                request.consent().consentMethod()
            );

        claimConsentRepository.save(consent);

        /*
        * 접수 후 기본 후처리가 완료된 것으로 보고
        * RECEIVED → IN_PROGRESS로 변경한다.
        */
        ClaimStatus previousStatus =
            claim.getStatus();

        claim.startProcessing();

        claimHistoryRepository.save(
            ClaimHistory.statusChangedBySystem(
                claim,
                previousStatus,
                claim.getStatus(),
                "접수 완료 후 진행중 상태로 자동 변경"
            )
        );

        eventPublisher.publishEvent(
            new ClaimCreatedEvent(
                claim.getId()
            )
        );

        return ClaimResponse.from(
            claim,
            consent
        );
    }

    /**
     * 생년월일 + 피해자명 기준 중복 의심 접수 조회.
     *
     * 중복이라고 접수를 막지 않는다.
     */
    @Transactional(readOnly = true)
    public List<ClaimDuplicateResponse> findDuplicates(
            String victimName,
            java.time.LocalDate victimBirthDate,
            Long accountId
    ) {
        if (victimName == null || victimName.isBlank()) {
            throw new IllegalArgumentException(
                    "피해자명은 필수입니다.");
        }

        if (victimBirthDate == null) {
            throw new IllegalArgumentException(
                    "생년월일은 필수입니다.");
        }

        ClaimAccessContext context = claimAccessContextResolver.resolveForCreate(
                accountId);

        return claimRepository
                .findAllByBranchIdAndVictimNameAndVictimBirthDateOrderByCreatedAtDesc(
                        context.branch().getId(),
                        victimName.trim(),
                        victimBirthDate)
                .stream()
                .map(ClaimDuplicateResponse::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<ClaimListResponse> findClaims(
            ClaimSearchCondition condition,
            Long accountId,
            Pageable pageable
    ) {
        ClaimSearchAccessContext context =
            claimAccessContextResolver.resolveForSearch(
                accountId
            );

        validateSearchPeriod(condition);

        Specification<Claim> spec =
            Specification.allOf(
                ClaimSpecification.receivedBetween(
                    condition.receivedFrom(),
                    condition.receivedTo()
                ),
                ClaimSpecification.accidentBetween(
                    condition.accidentFrom(),
                    condition.accidentTo()
                ),
                ClaimSpecification.progressStatusEquals(
                    condition.progressStatus()
                ),
                ClaimSpecification.receivedByNameContains(
                    condition.receivedByName()
                ),
                ClaimSpecification.victimNameContains(
                    condition.victimName()
                )
            );

        Role role = context.account().getRole();

        switch (role) {

            case ADMIN1, ADMIN2 -> {
                // 전체 조회
            }

            case ADMIN3 -> {
                if (context.hotel() == null) {
                    throw new IllegalStateException(
                        "ADMIN3 계정에 호텔 소속이 설정되어 있지 않습니다."
                    );
                }

                spec = spec.and(
                    ClaimSpecification.hotelIdEquals(
                        context.hotel().getId()
                    )
                );
            }

            case ADMIN4 -> {
                if (context.branchGroup() == null) {
                    throw new IllegalStateException(
                        "ADMIN4 계정에 관리 그룹이 설정되어 있지 않습니다."
                    );
                }

                List<Long> branchIds =
                    branchGroupMemberRepository
                        .findAllByBranchGroupId(
                            context.branchGroup().getId()
                        )
                        .stream()
                        .map(member ->
                            member.getBranch().getId()
                        )
                        .toList();

                spec = spec.and(
                    ClaimSpecification.branchIdIn(
                        branchIds
                    )
                );
            }

            case BRANCH_SHARED -> {
                if (context.branch() == null) {
                    throw new IllegalStateException(
                        "지점 공유계정에 지점 소속이 설정되어 있지 않습니다."
                    );
                }

                spec = spec.and(
                    ClaimSpecification.branchIdEquals(
                        context.branch().getId()
                    )
                );
            }

            default ->
                throw new AccessDeniedException(
                    "사고현황을 조회할 권한이 없습니다."
                );
        }

        return claimRepository
            .findAll(
                spec,
                pageable
            )
            .map(ClaimListResponse::from);
    }

    private void validateCreateRequest(
            ClaimCreateRequest request
    ) {
        validateVictimLanguage(
            request.victimType(),
            request.preferredLanguage()
        );

        validateAccidentDescription(
            request.accidentDescription()
        );

        if (request.consent() == null) {
            throw new IllegalArgumentException(
                "개인정보 동의 정보는 필수입니다."
            );
        }
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
    
    @Transactional(readOnly = true)
    public ClaimResponse findOne(
            Long claimId,
            Long accountId
    ) {
        ClaimSearchAccessContext context =
            claimAccessContextResolver.resolveForSearch(
                accountId
            );

        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        validateReadAccess(
            context,
            claim
        );

        ClaimConsent consent =
            claimConsentRepository
                .findByClaimId(claimId)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "접수건의 개인정보 동의 정보를 찾을 수 없습니다."
                    )
                );

        return ClaimResponse.from(
            claim,
            consent
        );
    }
    
    @Transactional
    public ClaimResponse updateClaim(
            Long claimId,
            ClaimUpdateRequest request,
            Long accountId
    ) {
        ClaimAccessContext context = claimAccessContextResolver.resolveForCreate(
                accountId);

        Claim claim = claimRepository
                .findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "접수건을 찾을 수 없습니다."));

        // 다른 지점 사고 수정 차단
        if (!claim.getBranch()
                .getId()
                .equals(context.branch().getId())) {

            throw new AccessDeniedException(
                    "다른 지점의 접수건은 수정할 수 없습니다.");
        }

        ClaimConsent consent = claimConsentRepository
                .findByClaimId(claimId)
                .orElseThrow(() -> new IllegalStateException(
                        "접수건의 개인정보 동의 정보를 찾을 수 없습니다."));

        validateVictimLanguage(
                request.victimType(),
                request.preferredLanguage()
        );
        
        validateAccidentDescription(
            request.accidentDescription()
        );

        if (request.consent() == null) {
            throw new IllegalArgumentException(
                "개인정보 동의 정보는 필수입니다."
            );
        }

        String beforeValue = buildClaimSnapshot(
                claim,
                consent);
        String beforeConsentValue =
                buildConsentSnapshot(consent);
    
        claim.update(
                request.victimName().trim(),
                request.victimPhone().trim(),
                request.victimBirthDate(),
                request.victimType(),
                request.preferredLanguage(),
                request.residenceSido().trim(),
                request.residenceSigungu().trim(),
                trimToNull(request.residenceDetail()),
                request.claimType(),
                request.accidentAt(),
                request.accidentDescription().trim(),
                request.receivedByName().trim(),
                request.receivedByExtension().trim());

        // 개인정보 동의 정보도 같이 수정
        if (request.consent().consentStatus() == ConsentStatus.OBTAINED) {

            consent.obtainConsent(
                    request.consent().consentObtainedAt(),
                    request.consent().consentMethod());

        } else {
            consent.markNotObtained();
        }

        String afterValue = buildClaimSnapshot(
                claim,
                consent
        );
        
        String afterConsentValue =
                buildConsentSnapshot(consent);
        
        if (beforeValue.equals(afterValue)) {
            throw new IllegalArgumentException(
                "변경된 내용이 없습니다."
            );
        }

        claimHistoryRepository.save(
                ClaimHistory.updatedByUser(
                        claim,
                        context.account(),
                        beforeValue,
                        afterValue));
        
        if (!beforeConsentValue.equals(afterConsentValue)) {
            claimHistoryRepository.save(
                ClaimHistory.consentUpdatedByUser(
                    claim,
                    context.account(),
                    beforeConsentValue,
                    afterConsentValue
                )
            );
        }

        return ClaimResponse.from(
                claim,
                consent);
    }
    
    private void validateVictimLanguage(
        VictimType victimType,
        PreferredLanguage preferredLanguage
    ) {
        if (victimType == VictimType.DOMESTIC
                && preferredLanguage != null) {

            throw new IllegalArgumentException(
                    "내국인은 사용 가능 언어를 선택할 수 없습니다.");
        }

        if (victimType == VictimType.FOREIGNER
                && preferredLanguage == null) {

            throw new IllegalArgumentException(
                    "외국인은 사용 가능 언어를 선택해야 합니다.");
        }
    }
    
    private String buildClaimSnapshot(
            Claim claim,
            ClaimConsent consent
    ) {
        return """
                {
                "victimName": "%s",
                "victimPhone": "%s",
                "victimBirthDate": "%s",
                "victimType": "%s",
                "preferredLanguage": "%s",
                "residenceSido": "%s",
                "residenceSigungu": "%s",
                "residenceDetail": "%s",
                "claimType": "%s",
                "accidentAt": "%s",
                "accidentDescription": "%s",
                "receivedByName": "%s",
                "receivedByExtension": "%s",
                "consentStatus": "%s",
                "consentObtainedAt": "%s",
                "consentMethod": "%s"
                }
                """.formatted(
                claim.getVictimName(),
                claim.getVictimPhone(),
                claim.getVictimBirthDate(),
                claim.getVictimType(),
                claim.getPreferredLanguage(),
                claim.getResidenceSido(),
                claim.getResidenceSigungu(),
                claim.getResidenceDetail(),
                claim.getClaimType(),
                claim.getAccidentAt(),
                claim.getAccidentDescription(),
                claim.getReceivedByName(),
                claim.getReceivedByExtension(),
                consent.getConsentStatus(),
                consent.getConsentObtainedAt(),
                consent.getConsentMethod());
    }
    
    @Transactional(readOnly = true)
    public Page<ClaimHistoryResponse> findHistories(
            Long claimId,
            Long accountId,
            Pageable pageable
    ) {
        ClaimSearchAccessContext context =
            claimAccessContextResolver.resolveForSearch(
                accountId
            );

        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        validateReadAccess(
            context,
            claim
        );

        return claimHistoryRepository
            .findByClaimId(
                claimId,
                pageable
            )
            .map(ClaimHistoryResponse::from);
    }
    
    private String buildConsentSnapshot(
        ClaimConsent consent
    ) {
        return """
                {
                "consentStatus": "%s",
                "consentObtainedAt": "%s",
                "consentMethod": "%s"
                }
                """.formatted(
                consent.getConsentStatus(),
                consent.getConsentObtainedAt(),
                consent.getConsentMethod());
    }
    
    private void validateAccidentDescription(
        String accidentDescription
    ) {
        if (accidentDescription == null
                || accidentDescription.isBlank()) {
            throw new IllegalArgumentException(
                    "사고경위는 필수입니다.");
        }

        if (accidentDescription.trim().length() > 200) {
            throw new IllegalArgumentException(
                    "사고경위는 200자 이하로 입력해주세요.");
        }
    }
    
    private void validateReadAccess(
            ClaimSearchAccessContext context,
            Claim claim
    ) {
        Role role = context.account().getRole();

        switch (role) {

            case ADMIN1, ADMIN2 -> {
                // 전체 조회 가능
            }

            case ADMIN3 -> {
                if (context.hotel() == null) {
                    throw new IllegalStateException(
                            "ADMIN3 계정에 호텔 소속이 설정되어 있지 않습니다.");
                }

                if (!claim.getHotel()
                        .getId()
                        .equals(context.hotel().getId())) {

                    throw new AccessDeniedException(
                            "소속 호텔의 사고만 조회할 수 있습니다.");
                }
            }

            case ADMIN4 -> {
                if (context.branchGroup() == null) {
                    throw new IllegalStateException(
                            "ADMIN4 계정에 관리 그룹이 설정되어 있지 않습니다.");
                }

                boolean accessible = branchGroupMemberRepository
                        .existsByBranchGroupIdAndBranchId(
                                context.branchGroup().getId(),
                                claim.getBranch().getId());

                if (!accessible) {
                    throw new AccessDeniedException(
                            "관리 범위에 포함된 지점의 사고만 조회할 수 있습니다.");
                }
            }

            case BRANCH_SHARED -> {
                if (context.branch() == null) {
                    throw new IllegalStateException(
                            "지점 공유계정에 지점 소속이 설정되어 있지 않습니다.");
                }

                if (!claim.getBranch()
                        .getId()
                        .equals(context.branch().getId())) {

                    throw new AccessDeniedException(
                            "다른 지점의 사고는 조회할 수 없습니다.");
                }
            }

            default ->
                throw new AccessDeniedException(
                        "사고현황을 조회할 권한이 없습니다.");
        }
    }
    
    @Transactional
    public void cancelClaim(
            Long claimId,
            Long accountId
    ) {
        ClaimSearchAccessContext context =
            claimAccessContextResolver.resolveForSearch(
                accountId
            );

        if (context.account().getRole() != Role.ADMIN1) {
            throw new AccessDeniedException(
                "접수취소는 최고관리자만 처리할 수 있습니다."
            );
        }

        Claim claim = claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );

        ClaimStatus previousStatus =
            claim.getStatus();

        claim.cancel(
            LocalDateTime.now(
                ZoneId.of("Asia/Seoul")
            )
        );

        claimHistoryRepository.save(
            ClaimHistory.cancelledByAdmin(
                claim,
                context.account(),
                previousStatus
            )
        );
    }
}
package com.example.backendlotte.claim.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.claim.dto.ClaimCreateRequest;
import com.example.backendlotte.claim.dto.ClaimDuplicateResponse;
import com.example.backendlotte.claim.dto.ClaimResponse;
import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimConsent;
import com.example.backendlotte.claim.repository.ClaimConsentRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.type.VictimType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimConsentRepository claimConsentRepository;

    private final ClaimNumberGenerator claimNumberGenerator;
    private final ClaimAccessContextResolver claimAccessContextResolver;

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
         * 현재 요구사항:
         * 접수 완료 후 사고현황에는 '진행중'으로 표시.
         *
         * 문자 발송 자체는 이후 별도 후처리 구조로 분리한다.
         */
        claim.startProcessing();

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
            java.time.LocalDate victimBirthDate
    ) {
        if (victimName == null
                || victimName.isBlank()) {
            throw new IllegalArgumentException(
                "피해자명은 필수입니다."
            );
        }

        if (victimBirthDate == null) {
            throw new IllegalArgumentException(
                "생년월일은 필수입니다."
            );
        }

        return claimRepository
            .findAllByVictimNameAndVictimBirthDateOrderByCreatedAtDesc(
                victimName.trim(),
                victimBirthDate
            )
            .stream()
            .map(ClaimDuplicateResponse::from)
            .toList();
    }

    private void validateCreateRequest(
            ClaimCreateRequest request
    ) {
        /*
         * 외국인만 사용 가능 언어를 선택한다.
         */
        if (request.victimType() == VictimType.DOMESTIC
                && request.preferredLanguage() != null) {

            throw new IllegalArgumentException(
                "내국인은 사용 가능 언어를 선택할 수 없습니다."
            );
        }

        if (request.victimType() == VictimType.FOREIGNER
                && request.preferredLanguage() == null) {

            throw new IllegalArgumentException(
                "외국인은 사용 가능 언어를 선택해야 합니다."
            );
        }

        /*
         * ClaimConsent에서도 검증하지만,
         * Service에서도 업무 규칙을 명확하게 검사한다.
         */
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
}
package com.example.backendlotte.claim.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.backendlotte.claim.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    Optional<Claim> findByClaimNumber(
        String claimNumber
    );

    boolean existsByClaimNumber(
        String claimNumber
    );

    /*
     * 피해자명 + 생년월일 기준 중복 의심 접수건 조회.
     * 실제 중복 차단이 아니라 경고용으로 사용한다.
     */
    List<Claim> findAllByVictimNameAndVictimBirthDateOrderByCreatedAtDesc(
        String victimName,
        LocalDate victimBirthDate
    );

    /*
     * 특정 지점의 접수건 조회.
     * 이후 사고현황 화면에서 사용한다.
     */
    List<Claim> findAllByBranchIdOrderByCreatedAtDesc(
        Long branchId
    );

    List<Claim> findAllByBranchIdAndVictimNameAndVictimBirthDateOrderByCreatedAtDesc(
        Long branchId,
        String victimName,
        LocalDate victimBirthDate
    );
}
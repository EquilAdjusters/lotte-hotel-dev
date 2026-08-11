package com.example.backendlotte.claim.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimProgressStatus;
import com.example.backendlotte.claim.type.ClaimStatus;

public final class ClaimSpecification {

    private ClaimSpecification() {
    }

    public static Specification<Claim> receivedBetween(
            LocalDate from,
            LocalDate to
    ) {
        return (root, query, cb) -> {

            if (from == null && to == null) {
                return null;
            }

            if (from != null && to != null) {
                LocalDateTime start =
                    from.atStartOfDay();

                LocalDateTime end =
                    to.plusDays(1).atStartOfDay();

                return cb.and(
                    cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        start
                    ),
                    cb.lessThan(
                        root.get("createdAt"),
                        end
                    )
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    from.atStartOfDay()
                );
            }

            return cb.lessThan(
                root.get("createdAt"),
                to.plusDays(1).atStartOfDay()
            );
        };
    }

    public static Specification<Claim> accidentBetween(
            LocalDate from,
            LocalDate to
    ) {
        return (root, query, cb) -> {

            if (from == null && to == null) {
                return null;
            }

            if (from != null && to != null) {
                return cb.and(
                    cb.greaterThanOrEqualTo(
                        root.get("accidentAt"),
                        from.atStartOfDay()
                    ),
                    cb.lessThan(
                        root.get("accidentAt"),
                        to.plusDays(1).atStartOfDay()
                    )
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(
                    root.get("accidentAt"),
                    from.atStartOfDay()
                );
            }

            return cb.lessThan(
                root.get("accidentAt"),
                to.plusDays(1).atStartOfDay()
            );
        };
    }

    public static Specification<Claim> receivedByNameContains(
            String receivedByName
    ) {
        return (root, query, cb) -> {

            if (receivedByName == null
                    || receivedByName.isBlank()) {
                return null;
            }

            return cb.like(
                root.get("receivedByName"),
                "%" + receivedByName.trim() + "%"
            );
        };
    }

    public static Specification<Claim> victimNameContains(
            String victimName
    ) {
        return (root, query, cb) -> {

            if (victimName == null
                    || victimName.isBlank()) {
                return null;
            }

            return cb.like(
                root.get("victimName"),
                "%" + victimName.trim() + "%"
            );
        };
    }

    public static Specification<Claim> progressStatusEquals(
            ClaimProgressStatus progressStatus
    ) {
        return (root, query, cb) -> {

            if (progressStatus == null) {
                return null;
            }

            return switch (progressStatus) {

                case IN_PROGRESS ->
                    cb.equal(
                            root.get("status"),
                            ClaimStatus.IN_PROGRESS);

                case CLOSED_PAID ->
                    cb.and(
                            cb.equal(
                                    root.get("status"),
                                    ClaimStatus.CLOSED),
                            cb.equal(
                                    root.get("closingResult"),
                                    ClaimClosingResult.INSURANCE_PAID));

                case CLOSED_EXEMPTED ->
                    cb.and(
                            cb.equal(
                                    root.get("status"),
                                    ClaimStatus.CLOSED),
                            cb.equal(
                                    root.get("closingResult"),
                                    ClaimClosingResult.EXEMPTED));
                                    
                case CANCELLED ->
                cb.equal(
                    root.get("status"),
                    ClaimStatus.CANCELLED
                );
            };
        };
    }
    
    public static Specification<Claim> branchIdEquals(
            Long branchId
    ) {
        return (root, query, cb) -> {

            if (branchId == null) {
                return null;
            }

            return cb.equal(
                root.get("branch").get("id"),
                branchId
            );
        };
    }

    public static Specification<Claim> hotelIdEquals(
            Long hotelId
    ) {
        return (root, query, cb) -> {

            if (hotelId == null) {
                return null;
            }

            return cb.equal(
                    root.get("branch")
                            .get("hotel")
                            .get("id"),
                    hotelId);
        };
    }
    
    public static Specification<Claim> branchIdIn(
        List<Long> branchIds
    ) {
        return (root, query, cb) -> {

            if (branchIds == null
                    || branchIds.isEmpty()) {

                // ADMIN4에게 연결된 지점이 없으면
                // 전체 조회가 아니라 0건 반환
                return cb.disjunction();
            }

            return root
                    .get("branch")
                    .get("id")
                    .in(branchIds);
        };
    }
    
    public static Specification<Claim> assignmentStatusEquals(
        Boolean assigned
    ) {
        return (root, query, cb) -> {

            if (assigned == null) {
                return null;
            }

            if (assigned) {
                return cb.and(
                    cb.isNotNull(
                        root.get("adjustingCompany")
                    ),
                    cb.isNotNull(
                        root.get("adjuster")
                    )
                );
            }

            return cb.or(
                cb.isNull(
                    root.get("adjustingCompany")
                ),
                cb.isNull(
                    root.get("adjuster")
                )
            );
        };
    }
}
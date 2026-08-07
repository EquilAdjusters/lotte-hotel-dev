package com.example.backendlotte.account.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backendlotte.account.dto.AccountSearchCondition;
import com.example.backendlotte.account.entity.Account;

import jakarta.persistence.criteria.Predicate;

public final class AccountSpecification {

    private AccountSpecification() {
    }

    public static Specification<Account> search(
            AccountSearchCondition condition
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (condition == null) {
                return criteriaBuilder.conjunction();
            }

            String keyword = normalizeKeyword(condition.keyword());

            if (keyword != null) {
                String pattern = "%" + keyword.toLowerCase() + "%";

                Predicate loginIdLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("loginId")),
                    pattern
                );

                Predicate displayNameLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("displayName")),
                    pattern
                );

                predicates.add(
                    criteriaBuilder.or(
                        loginIdLike,
                        displayNameLike
                    )
                );
            }

            if (condition.role() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("role"),
                        condition.role()
                    )
                );
            }

            if (condition.status() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("status"),
                        condition.status()
                    )
                );
            }

            if (condition.hotelCompanyId() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("hotelCompany").get("id"),
                        condition.hotelCompanyId()
                    )
                );
            }

            if (condition.hotelId() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("hotel").get("id"),
                        condition.hotelId()
                    )
                );
            }

            if (condition.branchId() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("branch").get("id"),
                        condition.branchId()
                    )
                );
            }

            if (condition.branchGroupId() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("branchGroup").get("id"),
                        condition.branchGroupId()
                    )
                );
            }

            return criteriaBuilder.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }

    private static String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}
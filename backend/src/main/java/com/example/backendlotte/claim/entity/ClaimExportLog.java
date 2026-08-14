package com.example.backendlotte.claim.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.account.entity.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim_export_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimExportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "account_id",
        nullable = false
    )
    private Account account;

    @Column(
        name = "search_condition",
        columnDefinition = "TEXT"
    )
    private String searchCondition;

    @Column(
        name = "exported_count",
        nullable = false
    )
    private int exportedCount;

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    private ClaimExportLog(
            Account account,
            String searchCondition,
            int exportedCount
    ) {
        this.account = account;
        this.searchCondition = searchCondition;
        this.exportedCount = exportedCount;
    }

    public static ClaimExportLog create(
            Account account,
            String searchCondition,
            int exportedCount
    ) {
        if (account == null) {
            throw new IllegalArgumentException(
                "다운로드 계정 정보는 필수입니다."
            );
        }

        if (exportedCount < 0) {
            throw new IllegalArgumentException(
                "다운로드 건수는 0 이상이어야 합니다."
            );
        }

        return new ClaimExportLog(
            account,
            searchCondition,
            exportedCount
        );
    }
}
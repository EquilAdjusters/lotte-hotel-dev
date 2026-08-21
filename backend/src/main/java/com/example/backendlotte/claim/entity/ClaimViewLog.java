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
@Table(name = "claim_view_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "claim_id",
        nullable = false
    )
    private Claim claim;

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
        name = "viewed_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime viewedAt;

    private ClaimViewLog(
            Claim claim,
            Account account
    ) {
        this.claim = claim;
        this.account = account;
    }

    public static ClaimViewLog create(
            Claim claim,
            Account account
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "조회한 접수건 정보는 필수입니다."
            );
        }

        if (account == null) {
            throw new IllegalArgumentException(
                "조회 계정 정보는 필수입니다."
            );
        }

        return new ClaimViewLog(
            claim,
            account
        );
    }
}

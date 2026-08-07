package com.example.backendlotte.claim.entity;

import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim_number_sequences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimNumberSequence extends BaseEntity {

    @Id
    @Column(
        name = "period",
        nullable = false,
        length = 4
    )
    private String period;

    @Column(
        name = "last_sequence",
        nullable = false
    )
    private int lastSequence;

    private ClaimNumberSequence(String period) {
        this.period = period;
        this.lastSequence = 0;
    }

    public static ClaimNumberSequence create(
            String period
    ) {
        if (period == null
                || !period.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                "접수번호 기간 형식이 올바르지 않습니다."
            );
        }

        return new ClaimNumberSequence(period);
    }

    public int nextSequence() {
        this.lastSequence++;

        return this.lastSequence;
    }
}
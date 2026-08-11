package com.example.backendlotte.adjusting.entity;

import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "adjusting_companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdjustingCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        length = 100
    )
    private String name;

    @Column(
        name = "business_number",
        length = 30
    )
    private String businessNumber;

    @Column(nullable = false)
    private boolean active = true;

    private AdjustingCompany(
            String name,
            String businessNumber
    ) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.active = true;
    }

    public static AdjustingCompany create(
            String name,
            String businessNumber
    ) {
        return new AdjustingCompany(
            name,
            businessNumber
        );
    }

    public void update(
            String name,
            String businessNumber
    ) {
        this.name = name;
        this.businessNumber = businessNumber;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
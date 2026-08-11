package com.example.backendlotte.insurance.entity;

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
@Table(name = "insurance_companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsuranceCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        length = 100,
        unique = true
    )
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    private InsuranceCompany(
            String name
    ) {
        this.name = name;
        this.active = true;
    }

    public static InsuranceCompany create(
            String name
    ) {
        return new InsuranceCompany(
            name
        );
    }

    public void updateName(
            String name
    ) {
        this.name = name;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
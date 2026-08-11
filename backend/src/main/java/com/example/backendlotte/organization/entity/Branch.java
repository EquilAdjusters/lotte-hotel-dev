package com.example.backendlotte.organization.entity;

import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.insurance.entity.InsuranceCompany;

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
@Table(name = "branches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Branch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_company_id")
    private InsuranceCompany insuranceCompany;

    @Column(
        name = "receipt_email",
        length = 200
    )
    
    private String receiptEmail;
    
    private Branch(
            Hotel hotel,
            String name
    ) {
        this.hotel = hotel;
        this.name = name;
        this.active = true;
    }

    public static Branch create(
            Hotel hotel,
            String name
    ) {
        return new Branch(
            hotel,
            name
        );
    }

    public void update(
            Hotel hotel,
            String name
    ) {
        this.hotel = hotel;
        this.name = name;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateInsuranceSetting(
        InsuranceCompany insuranceCompany,
        String receiptEmail
    ) {
        if (insuranceCompany == null) {
            throw new IllegalArgumentException(
                "보험사는 필수입니다."
            );
        }

        if (receiptEmail == null
                || receiptEmail.isBlank()) {
            throw new IllegalArgumentException(
                "접수메일은 필수입니다."
            );
        }

        this.insuranceCompany = insuranceCompany;
        this.receiptEmail = receiptEmail.trim();
    }
}
package com.example.backendlotte.adjusting.entity;

import java.util.HashSet;
import java.util.Set;

import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.organization.entity.HotelCompany;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable(
        name = "adjusting_company_hotel_companies",
        joinColumns = @JoinColumn(name = "adjusting_company_id"),
        inverseJoinColumns = @JoinColumn(name = "hotel_company_id")
    )
    private Set<HotelCompany> hotelCompanies = new HashSet<>();

    private AdjustingCompany(
            String name,
            String businessNumber,
            Set<HotelCompany> hotelCompanies
    ) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.active = true;
        this.hotelCompanies = hotelCompanies;
    }

    public static AdjustingCompany create(
            String name,
            String businessNumber,
            Set<HotelCompany> hotelCompanies
    ) {
        return new AdjustingCompany(
            name,
            businessNumber,
            hotelCompanies
        );
    }

    public void update(
            String name,
            String businessNumber
    ) {
        this.name = name;
        this.businessNumber = businessNumber;
    }

    public void updateHotelCompanies(
            Set<HotelCompany> hotelCompanies
    ) {
        this.hotelCompanies.clear();
        this.hotelCompanies.addAll(hotelCompanies);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
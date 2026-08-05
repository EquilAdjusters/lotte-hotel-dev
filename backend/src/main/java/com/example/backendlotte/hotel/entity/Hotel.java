package com.example.backendlotte.hotel.entity;

import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.organization.entity.HotelCompany;

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
@Table(name = "hotels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hotel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_company_id")
    private HotelCompany hotelCompany;

    private Hotel(
            HotelCompany hotelCompany,
            String name
    ) {
        this.hotelCompany = hotelCompany;
        this.name = name;
        this.active = true;
    }

    public static Hotel create(
            HotelCompany hotelCompany,
            String name
    ) {
        return new Hotel(
            hotelCompany,
            name
        );
    }

    public void update(
            HotelCompany hotelCompany,
            String name
    ) {
        this.hotelCompany = hotelCompany;
        this.name = name;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
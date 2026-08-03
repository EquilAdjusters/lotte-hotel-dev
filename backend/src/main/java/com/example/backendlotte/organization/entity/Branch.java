package com.example.backendlotte.organization.entity;

import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.hotel.entity.Hotel;

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
}
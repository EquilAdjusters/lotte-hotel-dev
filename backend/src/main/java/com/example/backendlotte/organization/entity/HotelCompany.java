package com.example.backendlotte.organization.entity;

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
@Table(name = "hotel_companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HotelCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active = true; // 호텔사 실제 삭제가 아니라 사용 중지 처리하기 위한 값
}
package com.example.backendlotte.organization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.HotelCompany;

public interface HotelCompanyRepository
        extends JpaRepository<HotelCompany, Long> {

    Optional<HotelCompany> findByName(String name);

    boolean existsByName(String name);
}
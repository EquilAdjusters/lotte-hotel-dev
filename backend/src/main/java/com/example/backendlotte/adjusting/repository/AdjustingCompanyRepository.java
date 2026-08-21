package com.example.backendlotte.adjusting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.adjusting.entity.AdjustingCompany;

public interface AdjustingCompanyRepository
extends JpaRepository<AdjustingCompany, Long> {

    List<AdjustingCompany>
        findAllByActiveTrueOrderByNameAsc();

    List<AdjustingCompany>
        findAllByOrderByNameAsc();

    List<AdjustingCompany>
        findAllByHotelCompanies_IdAndActiveTrueOrderByNameAsc(
            Long hotelCompanyId
        );
}
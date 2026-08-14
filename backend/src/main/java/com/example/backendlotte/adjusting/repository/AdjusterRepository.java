package com.example.backendlotte.adjusting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.adjusting.entity.Adjuster;

public interface AdjusterRepository
extends JpaRepository<Adjuster, Long> {

    List<Adjuster>
        findAllByAdjustingCompanyIdAndActiveTrueOrderByNameAsc(
            Long adjustingCompanyId
        );

    List<Adjuster>
        findAllByAdjustingCompanyIdOrderByNameAsc(
            Long adjustingCompanyId
        );
}
package com.example.backendlotte.insurance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.insurance.entity.InsuranceCompany;

public interface InsuranceCompanyRepository
extends JpaRepository<InsuranceCompany, Long> {

    List<InsuranceCompany>
        findAllByActiveTrueOrderByNameAsc();

    List<InsuranceCompany>
        findAllByOrderByNameAsc();
}
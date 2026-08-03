package com.example.backendlotte.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByHotelIdAndActiveTrue(Long hotelId);

    Optional<Branch> findByHotelIdAndName(Long hotelId, String name);

    boolean existsByHotelIdAndName(Long hotelId, String name);
}
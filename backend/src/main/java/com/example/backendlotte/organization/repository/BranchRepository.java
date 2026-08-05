package com.example.backendlotte.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    // 전체 지점 이름순 조회
    List<Branch> findAllByOrderByNameAsc();

    // 활성 지점만 이름순 조회
    List<Branch> findAllByActiveTrueOrderByNameAsc();

    // 특정 호텔의 전체 지점 조회
    List<Branch> findAllByHotelIdOrderByNameAsc(
        Long hotelId
    );

    // 특정 호텔의 활성 지점만 조회
    List<Branch> findAllByHotelIdAndActiveTrueOrderByNameAsc(
        Long hotelId
    );

    // 같은 호텔 안에서 같은 지점명이 있는지 확인
    boolean existsByHotelIdAndName(
        Long hotelId,
        String name
    );

    // 수정할 때 자기 자신을 제외하고 중복 검사
    boolean existsByHotelIdAndNameAndIdNot(
        Long hotelId,
        String name,
        Long branchId
    );

    // 특정 호텔과 지점명으로 조회
    Optional<Branch> findByHotelIdAndName(
        Long hotelId,
        String name
    );
}
package com.example.backendlotte.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.hotel.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // 전체 호텔을 이름순으로 조회
    List<Hotel> findAllByOrderByNameAsc();

    // 활성화된 호텔만 이름순으로 조회
    List<Hotel> findAllByActiveTrueOrderByNameAsc();

    List<Hotel> findAllByHotelCompanyIdOrderByNameAsc(
        Long hotelCompanyId
    );

    // 특정 호텔사에 속한 활성 호텔 조회
    List<Hotel> findAllByHotelCompanyIdAndActiveTrueOrderByNameAsc(
        Long hotelCompanyId
    );

    // 같은 호텔사 안에서 동일한 호텔명이 존재하는지 확인
    boolean existsByHotelCompanyIdAndName(
        Long hotelCompanyId,
        String name
    );

    // 수정 시 자기 자신을 제외하고 중복 호텔명이 있는지 확인
    boolean existsByHotelCompanyIdAndNameAndIdNot(
        Long hotelCompanyId,
        String name,
        Long id
    );

    // 특정 호텔사와 호텔명으로 조회
    Optional<Hotel> findByHotelCompanyIdAndName(
        Long hotelCompanyId,
        String name
    );
}
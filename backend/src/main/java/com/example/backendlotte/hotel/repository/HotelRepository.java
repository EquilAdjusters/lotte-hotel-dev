package com.example.backendlotte.hotel.repository;

import com.example.backendlotte.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
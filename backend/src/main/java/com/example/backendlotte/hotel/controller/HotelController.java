package com.example.backendlotte.hotel.controller;

import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;

    @GetMapping
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }
}
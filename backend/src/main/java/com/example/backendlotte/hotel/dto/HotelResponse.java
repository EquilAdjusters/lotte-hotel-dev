package com.example.backendlotte.hotel.dto;

import com.example.backendlotte.hotel.entity.Hotel;

public record HotelResponse(
        Long id,
        Long hotelCompanyId,
        String hotelCompanyName,
        String name,
        boolean active) {

    public static HotelResponse from(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getHotelCompany().getId(),
                hotel.getHotelCompany().getName(),
                hotel.getName(),
                hotel.isActive());
    }
}
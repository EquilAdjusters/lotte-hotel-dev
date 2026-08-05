package com.example.backendlotte.hotel.dto;

public record HotelUpdateRequest(
    Long hotelCompanyId,
    String name
) {
}
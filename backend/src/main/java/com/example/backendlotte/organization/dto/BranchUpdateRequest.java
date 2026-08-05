package com.example.backendlotte.organization.dto;

public record BranchUpdateRequest(
    Long hotelId,
    String name
) {
}
package com.example.backendlotte.organization.dto;

public record BranchCreateRequest(
    Long hotelId,
    String name
) {
}
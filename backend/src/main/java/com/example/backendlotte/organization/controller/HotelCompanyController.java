package com.example.backendlotte.organization.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.organization.dto.HotelCompanyCreateRequest;
import com.example.backendlotte.organization.dto.HotelCompanyResponse;
import com.example.backendlotte.organization.dto.HotelCompanyUpdateRequest;
import com.example.backendlotte.organization.service.HotelCompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hotel-companies")
@RequiredArgsConstructor
public class HotelCompanyController {

    private final HotelCompanyService hotelCompanyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<HotelCompanyResponse> create(
            @RequestBody HotelCompanyCreateRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(hotelCompanyService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3')")
    public List<HotelCompanyResponse> findAll(
            @RequestParam(
                defaultValue = "true"
            ) boolean activeOnly
    ) {
        return hotelCompanyService.findAll(activeOnly);
    }

    @PatchMapping("/{hotelCompanyId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public HotelCompanyResponse update(
            @PathVariable Long hotelCompanyId,
            @RequestBody HotelCompanyUpdateRequest request
    ) {
        return hotelCompanyService.update(
            hotelCompanyId,
            request
        );
    }

    @DeleteMapping("/{hotelCompanyId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> deactivate(
            @PathVariable Long hotelCompanyId
    ) {
        hotelCompanyService.deactivate(hotelCompanyId);

        return ResponseEntity.ok(
            Map.of(
                "code", "HOTEL_COMPANY_DEACTIVATED",
                "message", "호텔사가 사용 중지되었습니다."
            )
        );
    }
}
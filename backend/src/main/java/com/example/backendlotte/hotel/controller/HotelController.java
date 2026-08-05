package com.example.backendlotte.hotel.controller;

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

import com.example.backendlotte.hotel.dto.HotelCreateRequest;
import com.example.backendlotte.hotel.dto.HotelResponse;
import com.example.backendlotte.hotel.dto.HotelUpdateRequest;
import com.example.backendlotte.hotel.service.HotelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<HotelResponse> create(
            @RequestBody HotelCreateRequest request
    ) {
        HotelResponse response = hotelService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3')")
    public List<HotelResponse> findAll(
            @RequestParam(required = false)
            Long hotelCompanyId,

            @RequestParam(defaultValue = "true")
            boolean activeOnly
    ) {
        return hotelService.findAll(
            hotelCompanyId,
            activeOnly
        );
    }

    @GetMapping("/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2', 'ADMIN3')")
    public HotelResponse findOne(
            @PathVariable Long hotelId
    ) {
        return hotelService.findOne(hotelId);
    }

    @PatchMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public HotelResponse update(
            @PathVariable Long hotelId,
            @RequestBody HotelUpdateRequest request
    ) {
        return hotelService.update(
            hotelId,
            request
        );
    }

    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> deactivate(
            @PathVariable Long hotelId
    ) {
        hotelService.deactivate(hotelId);

        return ResponseEntity.ok(
            Map.of(
                "code", "HOTEL_DEACTIVATED",
                "message", "호텔이 사용 중지되었습니다."
            )
        );
    }

    @PatchMapping("/{hotelId}/activate")
    @PreAuthorize("hasRole('ADMIN1')")
    public HotelResponse activate(
            @PathVariable Long hotelId
    ) {
        return hotelService.activate(hotelId);
    }
}
package com.example.backendlotte.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.hotel.dto.HotelCreateRequest;
import com.example.backendlotte.hotel.dto.HotelResponse;
import com.example.backendlotte.hotel.dto.HotelUpdateRequest;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.hotel.repository.HotelRepository;
import com.example.backendlotte.organization.entity.HotelCompany;
import com.example.backendlotte.organization.repository.HotelCompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelCompanyRepository hotelCompanyRepository;

    @Transactional
    public HotelResponse create(
            HotelCreateRequest request
    ) {
        String name = normalizeName(request.name());

        HotelCompany hotelCompany =
            findActiveHotelCompany(request.hotelCompanyId());

        if (hotelRepository.existsByHotelCompanyIdAndName(
                hotelCompany.getId(),
                name
        )) {
            throw new IllegalArgumentException(
                "해당 호텔사에 이미 등록된 호텔명입니다."
            );
        }

        Hotel hotel = Hotel.create(
            hotelCompany,
            name
        );

        hotelRepository.save(hotel);

        return HotelResponse.from(hotel);
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> findAll(
            Long hotelCompanyId,
            boolean activeOnly
    ) {
        List<Hotel> hotels;

        if (hotelCompanyId != null) {
            hotels = activeOnly
                ? hotelRepository
                    .findAllByHotelCompanyIdAndActiveTrueOrderByNameAsc(
                        hotelCompanyId
                    )
                : hotelRepository
                    .findAllByHotelCompanyIdOrderByNameAsc(
                        hotelCompanyId
                    );
        } else {
            hotels = activeOnly
                ? hotelRepository
                    .findAllByActiveTrueOrderByNameAsc()
                : hotelRepository
                    .findAllByOrderByNameAsc();
        }

        return hotels.stream()
            .map(HotelResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public HotelResponse findOne(Long hotelId) {
        return HotelResponse.from(
            findById(hotelId)
        );
    }

    @Transactional
    public HotelResponse update(
            Long hotelId,
            HotelUpdateRequest request
    ) {
        Hotel hotel = findById(hotelId);

        HotelCompany hotelCompany =
            findActiveHotelCompany(request.hotelCompanyId());

        String name = normalizeName(request.name());

        if (hotelRepository
                .existsByHotelCompanyIdAndNameAndIdNot(
                    hotelCompany.getId(),
                    name,
                    hotelId
                )) {
            throw new IllegalArgumentException(
                "해당 호텔사에 이미 등록된 호텔명입니다."
            );
        }

        hotel.update(
            hotelCompany,
            name
        );

        return HotelResponse.from(hotel);
    }

    @Transactional
    public void deactivate(Long hotelId) {
        Hotel hotel = findById(hotelId);

        hotel.deactivate();
    }

    @Transactional
    public HotelResponse activate(Long hotelId) {
        Hotel hotel = findById(hotelId);

        hotel.activate();

        return HotelResponse.from(hotel);
    }

    private Hotel findById(Long hotelId) {
        return hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "호텔을 찾을 수 없습니다."
                )
            );
    }

    private HotelCompany findActiveHotelCompany(
            Long hotelCompanyId
    ) {
        if (hotelCompanyId == null) {
            throw new IllegalArgumentException(
                "호텔사 ID는 필수입니다."
            );
        }

        HotelCompany hotelCompany =
            hotelCompanyRepository
                .findById(hotelCompanyId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "호텔사를 찾을 수 없습니다."
                    )
                );

        if (!hotelCompany.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔사에는 호텔을 등록할 수 없습니다."
            );
        }

        return hotelCompany;
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "호텔명은 필수입니다."
            );
        }

        String normalized = name.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                "호텔명은 100자 이하로 입력해주세요."
            );
        }

        return normalized;
    }
}
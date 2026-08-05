package com.example.backendlotte.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.organization.dto.HotelCompanyCreateRequest;
import com.example.backendlotte.organization.dto.HotelCompanyResponse;
import com.example.backendlotte.organization.dto.HotelCompanyUpdateRequest;
import com.example.backendlotte.organization.entity.HotelCompany;
import com.example.backendlotte.organization.repository.HotelCompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelCompanyService {

    private final HotelCompanyRepository hotelCompanyRepository;

    @Transactional
    public HotelCompanyResponse create(
            HotelCompanyCreateRequest request
    ) {
        String name = normalizeName(request.name());

        if (hotelCompanyRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "이미 등록된 호텔사입니다."
            );
        }

        HotelCompany hotelCompany =
            HotelCompany.create(name);

        hotelCompanyRepository.save(hotelCompany);

        return HotelCompanyResponse.from(hotelCompany);
    }

    @Transactional(readOnly = true)
    public List<HotelCompanyResponse> findAll(
            boolean activeOnly
    ) {
        List<HotelCompany> hotelCompanies = activeOnly
            ? hotelCompanyRepository
                .findAllByActiveTrueOrderByNameAsc()
            : hotelCompanyRepository
                .findAllByOrderByNameAsc();

        return hotelCompanies.stream()
            .map(HotelCompanyResponse::from)
            .toList();
    }

    @Transactional
    public HotelCompanyResponse update(
            Long hotelCompanyId,
            HotelCompanyUpdateRequest request
    ) {
        HotelCompany hotelCompany =
            findById(hotelCompanyId);

        String name = normalizeName(request.name());

        hotelCompanyRepository.findByName(name)
            .filter(found ->
                !found.getId().equals(hotelCompanyId)
            )
            .ifPresent(found -> {
                throw new IllegalArgumentException(
                    "이미 등록된 호텔사입니다."
                );
            });

        hotelCompany.updateName(name);

        return HotelCompanyResponse.from(hotelCompany);
    }

    @Transactional
    public void deactivate(Long hotelCompanyId) {
        HotelCompany hotelCompany =
            findById(hotelCompanyId);

        hotelCompany.deactivate();
    }

    private HotelCompany findById(Long hotelCompanyId) {
        return hotelCompanyRepository
            .findById(hotelCompanyId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "호텔사를 찾을 수 없습니다."
                )
            );
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "호텔사명은 필수입니다."
            );
        }

        String normalized = name.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                "호텔사명은 100자 이하로 입력해주세요."
            );
        }

        return normalized;
    }
}
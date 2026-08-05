package com.example.backendlotte.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.hotel.repository.HotelRepository;
import com.example.backendlotte.organization.dto.BranchCreateRequest;
import com.example.backendlotte.organization.dto.BranchResponse;
import com.example.backendlotte.organization.dto.BranchUpdateRequest;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final HotelRepository hotelRepository;

    @Transactional
    public BranchResponse create(
            BranchCreateRequest request
    ) {
        String name = normalizeName(request.name());

        Hotel hotel = findActiveHotel(request.hotelId());

        if (branchRepository.existsByHotelIdAndName(
                hotel.getId(),
                name
        )) {
            throw new IllegalArgumentException(
                "해당 호텔에 이미 등록된 지점명입니다."
            );
        }

        Branch branch = Branch.create(
            hotel,
            name
        );

        branchRepository.save(branch);

        return BranchResponse.from(branch);
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findAll(
            Long hotelId,
            boolean activeOnly
    ) {
        List<Branch> branches;

        if (hotelId != null) {
            branches = activeOnly
                ? branchRepository
                    .findAllByHotelIdAndActiveTrueOrderByNameAsc(
                        hotelId
                    )
                : branchRepository
                    .findAllByHotelIdOrderByNameAsc(
                        hotelId
                    );
        } else {
            branches = activeOnly
                ? branchRepository
                    .findAllByActiveTrueOrderByNameAsc()
                : branchRepository
                    .findAllByOrderByNameAsc();
        }

        return branches.stream()
            .map(BranchResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse findOne(Long branchId) {
        return BranchResponse.from(
            findById(branchId)
        );
    }

    @Transactional
    public BranchResponse update(
            Long branchId,
            BranchUpdateRequest request
    ) {
        Branch branch = findById(branchId);

        Hotel hotel = findActiveHotel(request.hotelId());

        String name = normalizeName(request.name());

        if (branchRepository
                .existsByHotelIdAndNameAndIdNot(
                    hotel.getId(),
                    name,
                    branchId
                )) {
            throw new IllegalArgumentException(
                "해당 호텔에 이미 등록된 지점명입니다."
            );
        }

        branch.update(
            hotel,
            name
        );

        return BranchResponse.from(branch);
    }

    @Transactional
    public void deactivate(Long branchId) {
        Branch branch = findById(branchId);

        branch.deactivate();
    }

    @Transactional
    public BranchResponse activate(Long branchId) {
        Branch branch = findById(branchId);

        branch.activate();

        return BranchResponse.from(branch);
    }

    private Branch findById(Long branchId) {
        return branchRepository
            .findById(branchId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "지점을 찾을 수 없습니다."
                )
            );
    }

    private Hotel findActiveHotel(Long hotelId) {
        if (hotelId == null) {
            throw new IllegalArgumentException(
                "호텔 ID는 필수입니다."
            );
        }

        Hotel hotel = hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "호텔을 찾을 수 없습니다."
                )
            );

        if (!hotel.isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔에는 지점을 등록할 수 없습니다."
            );
        }

        if (hotel.getHotelCompany() == null
                || !hotel.getHotelCompany().isActive()) {
            throw new IllegalArgumentException(
                "사용 중지된 호텔사에 속한 호텔에는 지점을 등록할 수 없습니다."
            );
        }

        return hotel;
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "지점명은 필수입니다."
            );
        }

        String normalized = name.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                "지점명은 100자 이하로 입력해주세요."
            );
        }

        return normalized;
    }
}
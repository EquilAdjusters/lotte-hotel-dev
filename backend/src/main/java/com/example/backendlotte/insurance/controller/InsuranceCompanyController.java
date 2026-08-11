package com.example.backendlotte.insurance.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.insurance.dto.BranchInsuranceSettingRequest;
import com.example.backendlotte.insurance.dto.BranchInsuranceSettingResponse;
import com.example.backendlotte.insurance.dto.InsuranceCompanyCreateRequest;
import com.example.backendlotte.insurance.dto.InsuranceCompanyResponse;
import com.example.backendlotte.insurance.dto.InsuranceCompanyUpdateRequest;
import com.example.backendlotte.insurance.service.InsuranceCompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/insurance-companies")
@RequiredArgsConstructor
public class InsuranceCompanyController {

    private final InsuranceCompanyService insuranceCompanyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public List<InsuranceCompanyResponse> findActive() {
        return insuranceCompanyService.findActive();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<InsuranceCompanyResponse> create(
            @Valid @RequestBody
            InsuranceCompanyCreateRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                insuranceCompanyService.create(request)
            );
    }

    @PatchMapping("/{insuranceCompanyId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public InsuranceCompanyResponse update(
            @PathVariable Long insuranceCompanyId,
            @Valid @RequestBody
            InsuranceCompanyUpdateRequest request
    ) {
        return insuranceCompanyService.update(
            insuranceCompanyId,
            request
        );
    }

    @PostMapping("/{insuranceCompanyId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long insuranceCompanyId
    ) {
        insuranceCompanyService.deactivate(
            insuranceCompanyId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{insuranceCompanyId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public ResponseEntity<Void> activate(
            @PathVariable Long insuranceCompanyId
    ) {
        insuranceCompanyService.activate(
                insuranceCompanyId);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/branches/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public BranchInsuranceSettingResponse findBranchSetting(
            @PathVariable Long branchId
    ) {
        return insuranceCompanyService
            .findBranchSetting(branchId);
    }

    @PatchMapping("/branches/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN1', 'ADMIN2')")
    public BranchInsuranceSettingResponse updateBranchSetting(
            @PathVariable Long branchId,

            @Valid @RequestBody
            BranchInsuranceSettingRequest request
    ) {
        return insuranceCompanyService
            .updateBranchSetting(
                branchId,
                request
            );
    }
}
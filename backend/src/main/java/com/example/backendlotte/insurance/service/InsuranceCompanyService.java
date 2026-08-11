package com.example.backendlotte.insurance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.insurance.dto.BranchInsuranceSettingRequest;
import com.example.backendlotte.insurance.dto.BranchInsuranceSettingResponse;
import com.example.backendlotte.insurance.dto.InsuranceCompanyCreateRequest;
import com.example.backendlotte.insurance.dto.InsuranceCompanyResponse;
import com.example.backendlotte.insurance.dto.InsuranceCompanyUpdateRequest;
import com.example.backendlotte.insurance.entity.InsuranceCompany;
import com.example.backendlotte.insurance.repository.InsuranceCompanyRepository;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceCompanyService {

    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final BranchRepository branchRepository;

    @Transactional(readOnly = true)
    public List<InsuranceCompanyResponse> findActive() {
        return insuranceCompanyRepository
            .findAllByActiveTrueOrderByNameAsc()
            .stream()
            .map(InsuranceCompanyResponse::from)
            .toList();
    }

    @Transactional
    public InsuranceCompanyResponse create(
            InsuranceCompanyCreateRequest request
    ) {
        InsuranceCompany company =
            InsuranceCompany.create(
                request.name().trim()
            );

        insuranceCompanyRepository.save(company);

        return InsuranceCompanyResponse.from(company);
    }

    @Transactional
    public InsuranceCompanyResponse update(
            Long insuranceCompanyId,
            InsuranceCompanyUpdateRequest request
    ) {
        InsuranceCompany company =
            insuranceCompanyRepository
                .findById(insuranceCompanyId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "보험사를 찾을 수 없습니다."
                    )
                );

        company.updateName(
            request.name().trim()
        );

        return InsuranceCompanyResponse.from(company);
    }

    @Transactional
    public void deactivate(
            Long insuranceCompanyId
    ) {
        InsuranceCompany company =
            insuranceCompanyRepository
                .findById(insuranceCompanyId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "보험사를 찾을 수 없습니다."
                    )
                );

        company.deactivate();
    }

    @Transactional
    public void activate(
            Long insuranceCompanyId
    ) {
        InsuranceCompany company = insuranceCompanyRepository
                .findById(insuranceCompanyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "보험사를 찾을 수 없습니다."));

        company.activate();
    }
    
    @Transactional
    public BranchInsuranceSettingResponse updateBranchSetting(
            Long branchId,
            BranchInsuranceSettingRequest request
    ) {
        Branch branch = branchRepository
            .findById(branchId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "지점을 찾을 수 없습니다."
                )
            );

        InsuranceCompany insuranceCompany =
            insuranceCompanyRepository
                .findById(request.insuranceCompanyId())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "보험사를 찾을 수 없습니다."
                    )
                );

        if (!insuranceCompany.isActive()) {
            throw new IllegalStateException(
                "사용 중지된 보험사는 설정할 수 없습니다."
            );
        }

        branch.updateInsuranceSetting(
            insuranceCompany,
            request.receiptEmail()
        );

        return BranchInsuranceSettingResponse.from(
            branch
        );
    }

    @Transactional(readOnly = true)
    public BranchInsuranceSettingResponse findBranchSetting(
            Long branchId
    ) {
        Branch branch = branchRepository
            .findById(branchId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "지점을 찾을 수 없습니다."
                )
            );

        return BranchInsuranceSettingResponse.from(
            branch
        );
    }
}
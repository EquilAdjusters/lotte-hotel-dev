package com.example.backendlotte.insurance.dto;

import com.example.backendlotte.organization.entity.Branch;

public record BranchInsuranceSettingResponse(

    Long branchId,
    String branchName,

    Long insuranceCompanyId,
    String insuranceCompanyName,

    String receiptEmail

) {

    public static BranchInsuranceSettingResponse from(
            Branch branch
    ) {
        return new BranchInsuranceSettingResponse(
            branch.getId(),
            branch.getName(),

            branch.getInsuranceCompany() != null
                ? branch.getInsuranceCompany().getId()
                : null,

            branch.getInsuranceCompany() != null
                ? branch.getInsuranceCompany().getName()
                : null,

            branch.getReceiptEmail()
        );
    }
}
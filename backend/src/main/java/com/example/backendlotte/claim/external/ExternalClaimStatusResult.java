package com.example.backendlotte.claim.external;

import com.example.backendlotte.claim.type.ClaimClosingResult;

public record ExternalClaimStatusResult(

    String claimNumber,

    String adjustingCompanyName,
    String adjusterName,
    String adjusterPhone,

    boolean closed,
    ClaimClosingResult closingResult

) {
}
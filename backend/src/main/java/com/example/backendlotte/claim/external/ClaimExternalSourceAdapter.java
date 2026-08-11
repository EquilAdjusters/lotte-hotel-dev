package com.example.backendlotte.claim.external;

public interface ClaimExternalSourceAdapter {

    ExternalClaimStatusResult findByClaimNumber(
        String claimNumber
    );
}
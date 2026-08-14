package com.example.backendlotte.claim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClaimExportRow(

    String claimNumber,

    LocalDateTime receivedAt,
    LocalDateTime accidentAt,

    String hotelName,
    String branchName,

    String receivedByName,

    String victimName,
    LocalDate victimBirthDate,
    String victimPhone,

    String progressStatus,

    String adjustingCompanyName,
    String adjusterName,
    String adjusterPhone,

    String insuranceCompanyName

) {
}
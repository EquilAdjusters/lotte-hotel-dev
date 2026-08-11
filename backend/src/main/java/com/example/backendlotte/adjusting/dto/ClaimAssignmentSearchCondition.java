package com.example.backendlotte.adjusting.dto;

import java.time.LocalDate;

public record ClaimAssignmentSearchCondition(

    LocalDate receivedFrom,
    LocalDate receivedTo,

    String victimName,

    Boolean assigned

) {
}
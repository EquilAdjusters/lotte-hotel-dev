package com.example.backendlotte.claim.external.airtable.dto;

import java.util.List;

public record AirtableListResponse(
    List<AirtableRecord> records,
    String offset
) {
}
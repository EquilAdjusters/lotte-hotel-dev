package com.example.backendlotte.claim.external.airtable.dto;

import java.util.Map;

public record AirtableRecord(
    String id,
    Map<String, Object> fields
) {
}
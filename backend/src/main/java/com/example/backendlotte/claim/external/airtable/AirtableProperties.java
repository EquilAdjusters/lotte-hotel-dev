package com.example.backendlotte.claim.external.airtable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class AirtableProperties {

    private final String apiKey;
    private final String baseId;
    private final String tableId;

    public AirtableProperties(
            @Value("${app.external.airtable.api-key}")
            String apiKey,

            @Value("${app.external.airtable.base-id}")
            String baseId,

            @Value("${app.external.airtable.table-id}")
            String tableId
    ) {
        this.apiKey = apiKey;
        this.baseId = baseId;
        this.tableId = tableId;
    }
}
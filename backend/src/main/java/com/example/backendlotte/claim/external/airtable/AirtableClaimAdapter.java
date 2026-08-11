package com.example.backendlotte.claim.external.airtable;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.backendlotte.claim.external.ClaimExternalSourceAdapter;
import com.example.backendlotte.claim.external.ExternalClaimStatusResult;
import com.example.backendlotte.claim.external.airtable.dto.AirtableListResponse;
import com.example.backendlotte.claim.external.airtable.dto.AirtableRecord;
import com.example.backendlotte.claim.type.ClaimClosingResult;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AirtableClaimAdapter
implements ClaimExternalSourceAdapter {

    private final AirtableProperties properties;

    private final RestClient restClient =
        RestClient.create();

    @Override
    public ExternalClaimStatusResult findByClaimNumber(
            String claimNumber
    ) {
        String url =
            "https://api.airtable.com/v0/"
            + properties.getBaseId()
            + "/"
            + properties.getTableId();

        AirtableListResponse response =
            restClient
                .get()
                .uri(uriBuilder ->
                    uriBuilder
                        .path(url)
                        .queryParam(
                            "filterByFormula",
                            "{접수번호}='" + claimNumber + "'"
                        )
                        .build()
                )
                .header(
                    "Authorization",
                    "Bearer " + properties.getApiKey()
                )
                .retrieve()
                .body(AirtableListResponse.class);

        if (response == null
                || response.records() == null
                || response.records().isEmpty()) {

            throw new IllegalArgumentException(
                "Airtable에서 해당 접수건을 찾을 수 없습니다."
            );
        }

        AirtableRecord record =
            response.records().get(0);

        String status =
            getString(
                record,
                "진행현황"
            );

        ClaimClosingResult closingResult = null;

        boolean closed = false;

        if ("종결(보험금 지급)".equals(status)) {
            closed = true;
            closingResult =
                ClaimClosingResult.INSURANCE_PAID;

        } else if ("종결(면책)".equals(status)) {
            closed = true;
            closingResult =
                ClaimClosingResult.EXEMPTED;
        }

        return new ExternalClaimStatusResult(
            claimNumber,
            getString(record, "손사업체"),
            getString(record, "담당자"),
            getString(record, "전화번호"),
            closed,
            closingResult
        );
    }

    private String getString(
            AirtableRecord record,
            String fieldName
    ) {
        Object value =
            record.fields().get(fieldName);

        if (value == null) {
            return null;
        }

        return value.toString().trim();
    }
}
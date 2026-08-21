package com.example.backendlotte.account.dto;

import java.util.List;

public record LoginAccessLogIntegrityResponse(
    boolean intact,
    int checkedCount,
    List<Long> tamperedIds,
    List<Long> forkedIds
) {
}

package com.example.backendlotte.global.s3.dto;

public record PresignedDownloadResponse(
        String downloadUrl
) {
}
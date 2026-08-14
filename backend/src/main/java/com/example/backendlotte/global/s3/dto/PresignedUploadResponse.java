package com.example.backendlotte.global.s3.dto;

public record PresignedUploadResponse(
        String uploadUrl,
        String objectKey
) {
}
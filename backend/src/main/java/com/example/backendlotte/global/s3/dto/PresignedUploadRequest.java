package com.example.backendlotte.global.s3.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignedUploadRequest(
        @NotBlank String category,
        @NotBlank String originalFilename,
        @NotBlank String contentType
) {
}
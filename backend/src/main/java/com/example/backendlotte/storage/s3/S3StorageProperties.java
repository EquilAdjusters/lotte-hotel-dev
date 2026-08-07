package com.example.backendlotte.storage.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.s3")
public record S3StorageProperties(
    String bucket,
    String region,
    String baseDirectory
) {

    public S3StorageProperties {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException(
                "S3 버킷명은 필수입니다."
            );
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                "S3 리전은 필수입니다."
            );
        }

        if (baseDirectory == null
                || baseDirectory.isBlank()) {
            baseDirectory = "hotel-claim";
        }
    }
}
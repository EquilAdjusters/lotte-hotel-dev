package com.example.backendlotte.global.s3;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.prefix}")
    private String prefix;

    public PresignedUploadResult createUploadUrl(
            String category,
            String originalFilename,
            String contentType
    ) {

        String extension = getExtension(originalFilename);

        String storedFilename =
                UUID.randomUUID() + extension;

        String objectKey =
                prefix + "/" + category + "/" + storedFilename;

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType(contentType)
                        .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return new PresignedUploadResult(
                presignedRequest.url().toString(),
                objectKey
        );
    }

    private String getExtension(String filename) {

        int index = filename.lastIndexOf(".");

        if (index == -1) {
            return "";
        }

        return filename.substring(index);
    }

    public record PresignedUploadResult(
                    String uploadUrl,
                    String objectKey) {
    }
    
    public String createDownloadUrl(String objectKey) {

    GetObjectRequest getObjectRequest =
            GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

    GetObjectPresignRequest presignRequest =
            GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

    PresignedGetObjectRequest presignedRequest =
            s3Presigner.presignGetObject(presignRequest);

    return presignedRequest.url().toString();
}
}
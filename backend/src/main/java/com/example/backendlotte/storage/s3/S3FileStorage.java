package com.example.backendlotte.storage.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendlotte.storage.FileStorage;
import com.example.backendlotte.storage.FileStorageException;
import com.example.backendlotte.storage.StoredFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@ConditionalOnProperty(
    prefix = "app.storage.s3",
    name = "enabled",
    havingValue = "false",
    matchIfMissing = true
)
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private static final long MAX_FILE_SIZE =
        20L * 1024L * 1024L;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
        Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp"
        );

    private final S3Client s3Client;
    private final S3StorageProperties properties;

    @Override
    public StoredFile upload(
            String directory,
            MultipartFile file
    ) {
        validateFile(file);

        String originalFileName =
            sanitizeOriginalFileName(
                file.getOriginalFilename()
            );

        String extension =
            extractExtension(originalFileName);

        String storedFileName =
            UUID.randomUUID()
                + extension;

        String objectKey =
            createObjectKey(
                directory,
                storedFileName
            );

        String contentType =
            normalizeContentType(
                file.getContentType()
            );

        PutObjectRequest request =
            PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try (
            InputStream inputStream =
                file.getInputStream()
        ) {
            s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                    inputStream,
                    file.getSize()
                )
            );

            return new StoredFile(
                originalFileName,
                storedFileName,
                objectKey,
                contentType,
                file.getSize()
            );

        } catch (IOException exception) {
            throw new FileStorageException(
                "업로드 파일을 읽을 수 없습니다.",
                exception
            );

        } catch (S3Exception exception) {
            throw new FileStorageException(
                "S3 파일 업로드에 실패했습니다.",
                exception
            );
        }
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null
                || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                "삭제할 파일 객체 키는 필수입니다."
            );
        }

        DeleteObjectRequest request =
            DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);

        } catch (S3Exception exception) {
            throw new FileStorageException(
                "S3 파일 삭제에 실패했습니다.",
                exception
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                "업로드 파일은 필수입니다."
            );
        }

        if (file.getSize() <= 0) {
            throw new IllegalArgumentException(
                "비어 있는 파일은 업로드할 수 없습니다."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "첨부파일은 20MB 이하만 업로드할 수 있습니다."
            );
        }

        String contentType =
            normalizeContentType(
                file.getContentType()
            );

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                "PDF, JPG, PNG, WEBP 파일만 업로드할 수 있습니다."
            );
        }

        if (file.getOriginalFilename() == null
                || file.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException(
                "원본 파일명이 없습니다."
            );
        }
    }

    private String sanitizeOriginalFileName(
            String originalFileName
    ) {
        String normalized =
            originalFileName
                .replace("\\", "/");

        String fileName =
            normalized.substring(
                normalized.lastIndexOf("/") + 1
            );

        if (fileName.isBlank()
                || fileName.equals(".")
                || fileName.equals("..")) {
            throw new IllegalArgumentException(
                "파일명이 올바르지 않습니다."
            );
        }

        if (fileName.length() > 255) {
            throw new IllegalArgumentException(
                "파일명은 255자 이하만 가능합니다."
            );
        }

        return fileName;
    }

    private String extractExtension(
            String fileName
    ) {
        int dotIndex =
            fileName.lastIndexOf('.');

        if (dotIndex < 0
                || dotIndex == fileName.length() - 1) {
            return "";
        }

        String extension =
            fileName
                .substring(dotIndex)
                .toLowerCase(Locale.ROOT);

        if (extension.length() > 10) {
            throw new IllegalArgumentException(
                "파일 확장자가 올바르지 않습니다."
            );
        }

        return extension;
    }

    private String createObjectKey(
            String directory,
            String storedFileName
    ) {
        String normalizedBase =
            normalizeDirectory(
                properties.baseDirectory()
            );

        String normalizedDirectory =
            normalizeDirectory(directory);

        return normalizedBase
            + "/"
            + normalizedDirectory
            + "/"
            + storedFileName;
    }

    private String normalizeDirectory(
            String directory
    ) {
        if (directory == null
                || directory.isBlank()) {
            throw new IllegalArgumentException(
                "파일 저장 경로는 필수입니다."
            );
        }

        String normalized =
            directory
                .trim()
                .replace("\\", "/")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");

        if (normalized.isBlank()
                || normalized.contains("..")) {
            throw new IllegalArgumentException(
                "파일 저장 경로가 올바르지 않습니다."
            );
        }

        return normalized;
    }

    private String normalizeContentType(
            String contentType
    ) {
        if (contentType == null
                || contentType.isBlank()) {
            return "application/octet-stream";
        }

        return contentType
            .trim()
            .toLowerCase(Locale.ROOT);
    }
}
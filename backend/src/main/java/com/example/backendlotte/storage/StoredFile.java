package com.example.backendlotte.storage;

public record StoredFile(
    String originalFileName,
    String storedFileName,
    String objectKey,
    String contentType,
    long fileSize
) {
}
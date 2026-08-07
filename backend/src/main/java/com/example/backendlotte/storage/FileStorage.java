package com.example.backendlotte.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    StoredFile upload(
        String directory,
        MultipartFile file
    );

    void delete(
        String objectKey
    );
}
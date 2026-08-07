package com.example.backendlotte.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@ConditionalOnProperty(
    prefix = "app.storage.s3",
    name = "enabled",
    havingValue = "false",
    matchIfMissing = true
)
public class DisabledFileStorage implements FileStorage {

    @Override
    public StoredFile upload(
            String directory,
            MultipartFile file
    ) {
        throw new FileStorageException(
            "현재 파일 업로드 기능이 비활성화되어 있습니다."
        );
    }

    @Override
    public void delete(String objectKey) {
        throw new FileStorageException(
            "현재 파일 저장소 기능이 비활성화되어 있습니다."
        );
    }
}
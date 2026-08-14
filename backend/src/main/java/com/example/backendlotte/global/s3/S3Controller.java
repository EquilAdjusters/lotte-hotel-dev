package com.example.backendlotte.global.s3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backendlotte.global.s3.dto.PresignedUploadRequest;
import com.example.backendlotte.global.s3.dto.PresignedUploadResponse;
import com.example.backendlotte.global.s3.dto.PresignedDownloadResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/presigned-upload")
    public ResponseEntity<PresignedUploadResponse> createPresignedUploadUrl(
            @Valid @RequestBody PresignedUploadRequest request
    ) {

            S3Service.PresignedUploadResult result = s3Service.createUploadUrl(
                            request.category(),
                            request.originalFilename(),
                            request.contentType());

            return ResponseEntity.ok(
                            new PresignedUploadResponse(
                                            result.uploadUrl(),
                                            result.objectKey()));
    }
    
    @GetMapping("/presigned-download")
        public ResponseEntity<PresignedDownloadResponse> createPresignedDownloadUrl(
                @RequestParam String objectKey
        ) {

        String downloadUrl =
                s3Service.createDownloadUrl(objectKey);

        return ResponseEntity.ok(
                new PresignedDownloadResponse(downloadUrl)
        );
        }
}
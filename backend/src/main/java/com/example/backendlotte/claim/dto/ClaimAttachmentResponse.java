package com.example.backendlotte.claim.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.claim.entity.ClaimAttachment;
import com.example.backendlotte.claim.type.ClaimAttachmentType;

public record ClaimAttachmentResponse(

    Long id,
    Long claimId,

    ClaimAttachmentType attachmentType,

    String originalFileName,
    String contentType,
    long fileSize,

    Long uploadedByAccountId,
    LocalDateTime createdAt

) {

    public static ClaimAttachmentResponse from(
            ClaimAttachment attachment
    ) {
        return new ClaimAttachmentResponse(
            attachment.getId(),
            attachment.getClaim().getId(),
            attachment.getAttachmentType(),
            attachment.getOriginalFileName(),
            attachment.getContentType(),
            attachment.getFileSize(),
            attachment.getUploadedByAccount().getId(),
            attachment.getCreatedAt()
        );
    }
}
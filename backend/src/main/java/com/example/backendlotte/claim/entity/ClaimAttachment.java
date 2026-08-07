package com.example.backendlotte.claim.entity;

import java.time.LocalDateTime;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.claim.type.ClaimAttachmentType;
import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim_attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "claim_id",
        nullable = false
    )
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "uploaded_by_account_id",
        nullable = false
    )
    private Account uploadedByAccount;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "attachment_type",
        nullable = false,
        length = 30
    )
    private ClaimAttachmentType attachmentType;

    @Column(
        name = "original_file_name",
        nullable = false,
        length = 255
    )
    private String originalFileName;

    @Column(
        name = "stored_file_name",
        nullable = false,
        length = 255
    )
    private String storedFileName;

    @Column(
        name = "object_key",
        nullable = false,
        length = 500,
        unique = true
    )
    private String objectKey;

    @Column(
        name = "content_type",
        nullable = false,
        length = 100
    )
    private String contentType;

    @Column(
        name = "file_size",
        nullable = false
    )
    private long fileSize;

    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_account_id")
    private Account deletedByAccount;

    private ClaimAttachment(
            Claim claim,
            Account uploadedByAccount,
            ClaimAttachmentType attachmentType,
            String originalFileName,
            String storedFileName,
            String objectKey,
            String contentType,
            long fileSize
    ) {
        validateCreateValues(
            claim,
            uploadedByAccount,
            attachmentType,
            originalFileName,
            storedFileName,
            objectKey,
            contentType,
            fileSize
        );

        this.claim = claim;
        this.uploadedByAccount = uploadedByAccount;
        this.attachmentType = attachmentType;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.fileSize = fileSize;

        this.deleted = false;
        this.deletedAt = null;
        this.deletedByAccount = null;
    }

    public static ClaimAttachment create(
            Claim claim,
            Account uploadedByAccount,
            ClaimAttachmentType attachmentType,
            String originalFileName,
            String storedFileName,
            String objectKey,
            String contentType,
            long fileSize
    ) {
        return new ClaimAttachment(
            claim,
            uploadedByAccount,
            attachmentType,
            originalFileName,
            storedFileName,
            objectKey,
            contentType,
            fileSize
        );
    }

    public void delete(Account deletedByAccount) {
        if (this.deleted) {
            throw new IllegalStateException(
                "이미 삭제된 첨부파일입니다."
            );
        }

        if (deletedByAccount == null) {
            throw new IllegalArgumentException(
                "첨부파일 삭제 계정 정보는 필수입니다."
            );
        }

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedByAccount = deletedByAccount;
    }

    private static void validateCreateValues(
            Claim claim,
            Account uploadedByAccount,
            ClaimAttachmentType attachmentType,
            String originalFileName,
            String storedFileName,
            String objectKey,
            String contentType,
            long fileSize
    ) {
        if (claim == null) {
            throw new IllegalArgumentException(
                "접수건 정보는 필수입니다."
            );
        }

        if (uploadedByAccount == null) {
            throw new IllegalArgumentException(
                "업로드 계정 정보는 필수입니다."
            );
        }

        if (attachmentType == null) {
            throw new IllegalArgumentException(
                "첨부파일 종류는 필수입니다."
            );
        }

        if (originalFileName == null
                || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                "원본 파일명은 필수입니다."
            );
        }

        if (storedFileName == null
                || storedFileName.isBlank()) {
            throw new IllegalArgumentException(
                "저장 파일명은 필수입니다."
            );
        }

        if (objectKey == null
                || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                "파일 객체 키는 필수입니다."
            );
        }

        if (contentType == null
                || contentType.isBlank()) {
            throw new IllegalArgumentException(
                "파일 MIME 타입은 필수입니다."
            );
        }

        if (fileSize <= 0) {
            throw new IllegalArgumentException(
                "파일 크기는 0보다 커야 합니다."
            );
        }
    }
}
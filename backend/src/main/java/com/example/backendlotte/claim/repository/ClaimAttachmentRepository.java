package com.example.backendlotte.claim.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.claim.entity.ClaimAttachment;
import com.example.backendlotte.claim.type.ClaimAttachmentType;

public interface ClaimAttachmentRepository
        extends JpaRepository<ClaimAttachment, Long> {

    List<ClaimAttachment>
        findAllByClaimIdAndDeletedFalseOrderByCreatedAtAsc(
            Long claimId
        );

    List<ClaimAttachment>
        findAllByClaimIdAndAttachmentTypeAndDeletedFalseOrderByCreatedAtAsc(
            Long claimId,
            ClaimAttachmentType attachmentType
        );

    Optional<ClaimAttachment>
        findByIdAndDeletedFalse(
            Long attachmentId
        );

    boolean existsByObjectKey(
        String objectKey
    );
}
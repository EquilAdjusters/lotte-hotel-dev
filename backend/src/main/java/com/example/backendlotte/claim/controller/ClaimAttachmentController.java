package com.example.backendlotte.claim.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendlotte.auth.security.CustomUserDetails;
import com.example.backendlotte.claim.dto.ClaimAttachmentResponse;
import com.example.backendlotte.claim.service.ClaimAttachmentService;
import com.example.backendlotte.claim.type.ClaimAttachmentType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/claims/{claimId}/attachments")
@RequiredArgsConstructor
public class ClaimAttachmentController {

    private final ClaimAttachmentService claimAttachmentService;

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public ResponseEntity<ClaimAttachmentResponse> upload(
            @PathVariable Long claimId,

            @RequestParam
            ClaimAttachmentType attachmentType,

            @RequestParam("file")
            MultipartFile file,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        ClaimAttachmentResponse response =
            claimAttachmentService.upload(
                claimId,
                attachmentType,
                file,
                user.getAccountId()
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public List<ClaimAttachmentResponse> findAll(
            @PathVariable Long claimId,
            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        return claimAttachmentService.findAll(
            claimId,
            user.getAccountId()
        );
    }

    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("hasRole('BRANCH_SHARED')")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long claimId,
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal
            CustomUserDetails user
    ) {
        claimAttachmentService.delete(
            claimId,
            attachmentId,
            user.getAccountId()
        );

        return ResponseEntity.ok(
            Map.of(
                "code", "ATTACHMENT_DELETED",
                "message", "첨부파일이 삭제 처리되었습니다."
            )
        );
    }
}
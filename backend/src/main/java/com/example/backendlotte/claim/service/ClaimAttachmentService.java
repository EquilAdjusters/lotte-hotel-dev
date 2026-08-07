package com.example.backendlotte.claim.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.claim.dto.ClaimAttachmentResponse;
import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimAttachment;
import com.example.backendlotte.claim.repository.ClaimAttachmentRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.type.ClaimAttachmentType;
import com.example.backendlotte.storage.FileStorage;
import com.example.backendlotte.storage.StoredFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimAttachmentService {

    private final ClaimRepository claimRepository;
    private final ClaimAttachmentRepository claimAttachmentRepository;
    private final AccountRepository accountRepository;

    private final FileStorage fileStorage;

    @Transactional
    public ClaimAttachmentResponse upload(
            Long claimId,
            ClaimAttachmentType attachmentType,
            MultipartFile file,
            Long accountId
    ) {
        if (attachmentType == null) {
            throw new IllegalArgumentException(
                "첨부파일 종류는 필수입니다."
            );
        }

        Account account =
            findActiveAccount(accountId);

        Claim claim =
            findClaim(claimId);

        validateAccess(
            account,
            claim
        );

        /*
         * 접수번호 기준으로 디렉터리를 분리한다.
         *
         * 실제 objectKey 예:
         * hotel-claim/docker/claims/2608-0001/uuid.pdf
         */
        String directory =
            "claims/" + claim.getClaimNumber();

        StoredFile storedFile =
            fileStorage.upload(
                directory,
                file
            );

        try {
            ClaimAttachment attachment =
                ClaimAttachment.create(
                    claim,
                    account,
                    attachmentType,
                    storedFile.originalFileName(),
                    storedFile.storedFileName(),
                    storedFile.objectKey(),
                    storedFile.contentType(),
                    storedFile.fileSize()
                );

            claimAttachmentRepository.save(attachment);

            return ClaimAttachmentResponse.from(
                attachment
            );

        } catch (RuntimeException exception) {

            /*
             * S3 업로드 성공 후 DB 저장이 실패하면
             * S3에 고아 파일이 남으므로 보상 삭제한다.
             */
            try {
                fileStorage.delete(
                    storedFile.objectKey()
                );
            } catch (RuntimeException ignored) {
                /*
                 * 원래 DB 예외를 유지한다.
                 * 실제 운영에서는 여기에도 로그를 남길 예정.
                 */
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ClaimAttachmentResponse> findAll(
            Long claimId,
            Long accountId
    ) {
        Account account =
            findActiveAccount(accountId);

        Claim claim =
            findClaim(claimId);

        validateAccess(
            account,
            claim
        );

        return claimAttachmentRepository
            .findAllByClaimIdAndDeletedFalseOrderByCreatedAtAsc(
                claimId
            )
            .stream()
            .map(ClaimAttachmentResponse::from)
            .toList();
    }

    @Transactional
    public void delete(
            Long claimId,
            Long attachmentId,
            Long accountId
    ) {
        Account account =
            findActiveAccount(accountId);

        Claim claim =
            findClaim(claimId);

        validateAccess(
            account,
            claim
        );

        ClaimAttachment attachment =
            claimAttachmentRepository
                .findByIdAndDeletedFalse(attachmentId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "첨부파일을 찾을 수 없습니다."
                    )
                );

        if (!attachment.getClaim()
                .getId()
                .equals(claim.getId())) {

            throw new IllegalArgumentException(
                "해당 접수건의 첨부파일이 아닙니다."
            );
        }

        /*
         * 실제 S3 파일은 지금 삭제하지 않는다.
         * 개인정보/증적 보유기간 정책에 따라 나중에 파기한다.
         */
        attachment.delete(account);
    }

    private Account findActiveAccount(
            Long accountId
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException(
                "로그인 계정 정보가 없습니다."
            );
        }

        Account account =
            accountRepository
                .findById(accountId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "로그인 계정을 찾을 수 없습니다."
                    )
                );

        if (account.getStatus()
                != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                "활성 계정만 첨부파일을 처리할 수 있습니다."
            );
        }

        return account;
    }

    private Claim findClaim(
            Long claimId
    ) {
        if (claimId == null) {
            throw new IllegalArgumentException(
                "접수건 ID는 필수입니다."
            );
        }

        return claimRepository
            .findById(claimId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "접수건을 찾을 수 없습니다."
                )
            );
    }

    private void validateAccess(
            Account account,
            Claim claim
    ) {
        /*
         * 현재 사고접수 화면은 BRANCH_SHARED 기준.
         *
         * 자기 지점의 사고건만 파일 접근 가능.
         */
        if (account.getRole()
                == Role.BRANCH_SHARED) {

            if (account.getBranch() == null) {
                throw new IllegalStateException(
                    "계정의 지점 소속 정보가 없습니다."
                );
            }

            if (!account.getBranch()
                    .getId()
                    .equals(claim.getBranch().getId())) {

                throw new AccessDeniedException(
                    "다른 지점의 접수건에는 접근할 수 없습니다."
                );
            }

            return;
        }

        /*
         * ADMIN1 등 관리자 첨부파일 접근 범위는
         * 사고현황 조회권한 구현 시 함께 확장한다.
         */
        throw new AccessDeniedException(
            "현재 계정은 첨부파일을 처리할 수 없습니다."
        );
    }
}
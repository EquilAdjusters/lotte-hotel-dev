package com.example.backendlotte.claim.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.claim.dto.ClaimSearchCondition;
import com.example.backendlotte.claim.entity.Claim;
import com.example.backendlotte.claim.entity.ClaimExportLog;
import com.example.backendlotte.claim.repository.ClaimExportLogRepository;
import com.example.backendlotte.claim.repository.ClaimRepository;
import com.example.backendlotte.claim.specification.ClaimSpecification;
import com.example.backendlotte.global.util.PersonalDataMasker;
import com.example.backendlotte.organization.repository.BranchGroupMemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class ClaimExportService {

    private final ClaimRepository claimRepository;
    private final ClaimExportLogRepository claimExportLogRepository;
    private final AccountRepository accountRepository;
    private final ClaimAccessContextResolver claimAccessContextResolver;
    private final BranchGroupMemberRepository branchGroupMemberRepository;

    @Transactional
    public byte[] exportClaims(
            ClaimSearchCondition condition,
            Long accountId
    ) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "로그인 계정을 찾을 수 없습니다."
                )
            );

        validateExportRole(account);

        ClaimSearchAccessContext context =
            claimAccessContextResolver.resolveForSearch(
                accountId
            );

        Specification<Claim> spec =
            Specification.allOf(
                ClaimSpecification.receivedBetween(
                    condition.receivedFrom(),
                    condition.receivedTo()
                ),
                ClaimSpecification.accidentBetween(
                    condition.accidentFrom(),
                    condition.accidentTo()
                ),
                ClaimSpecification.progressStatusEquals(
                    condition.progressStatus()
                ),
                ClaimSpecification.receivedByNameContains(
                    condition.receivedByName()
                ),
                ClaimSpecification.victimNameContains(
                    condition.victimName()
                )
            );

        spec = applyAccessScope(
            spec,
            context
        );

        List<Claim> claims =
            claimRepository.findAll(
                spec,
                org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.DESC,
                    "createdAt"
                )
            );

        byte[] excel =
            createExcel(claims);

        claimExportLogRepository.save(
            ClaimExportLog.create(
                account,
                buildSearchConditionLog(condition),
                claims.size()
            )
        );

        return excel;
    }

    private void validateExportRole(
            Account account
    ) {
        Role role = account.getRole();

        if (role != Role.ADMIN1
                && role != Role.ADMIN2
                && role != Role.ADMIN3
                && role != Role.ADMIN4) {
            throw new AccessDeniedException(
                "사고현황 다운로드 권한이 없습니다."
            );
        }
    }

    private Specification<Claim> applyAccessScope(
            Specification<Claim> spec,
            ClaimSearchAccessContext context
    ) {
        Role role =
            context.account().getRole();

        return switch (role) {

            case ADMIN1, ADMIN2 ->
                spec;

            case ADMIN3 -> {
                if (context.hotel() == null) {
                    throw new IllegalStateException(
                        "ADMIN3 계정에 호텔 소속이 설정되어 있지 않습니다."
                    );
                }

                yield spec.and(
                    ClaimSpecification.hotelIdEquals(
                        context.hotel().getId()
                    )
                );
            }

            case ADMIN4 -> {
                if (context.branchGroup() == null) {
                    throw new IllegalStateException(
                        "ADMIN4 계정에 관리 그룹이 설정되어 있지 않습니다."
                    );
                }

                List<Long> branchIds =
                    branchGroupMemberRepository
                        .findAllByBranchGroupId(
                            context.branchGroup().getId()
                        )
                        .stream()
                        .map(member ->
                            member.getBranch().getId()
                        )
                        .toList();

                yield spec.and(
                    ClaimSpecification.branchIdIn(
                        branchIds
                    )
                );
            }

            default ->
                throw new AccessDeniedException(
                    "사고현황 다운로드 권한이 없습니다."
                );
        };
    }

    private byte[] createExcel(
            List<Claim> claims
    ) {
        try (
            XSSFWorkbook workbook =
                new XSSFWorkbook();

            ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream()
        ) {
            Sheet sheet =
                workbook.createSheet(
                    "사고현황"
                );

            createHeader(sheet);

            int rowIndex = 1;

            for (Claim claim : claims) {
                createDataRow(
                    sheet,
                    rowIndex++,
                    claim
                );
            }

            for (int i = 0; i < 14; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (Exception exception) {
            throw new IllegalStateException(
                "사고현황 엑셀 파일 생성에 실패했습니다.",
                exception
            );
        }
    }

    private void createHeader(
            Sheet sheet
    ) {
        Row row =
            sheet.createRow(0);

        String[] headers = {
            "접수번호",
            "접수일시",
            "사고일시",
            "호텔",
            "지점",
            "접수자명",
            "피해자명",
            "생년월일",
            "피해자 연락처",
            "진행현황",
            "손해사정업체",
            "담당자명",
            "담당자 연락처",
            "보험사"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell =
                row.createCell(i);

            cell.setCellValue(
                headers[i]
            );
        }
    }

    private void createDataRow(
            Sheet sheet,
            int rowIndex,
            Claim claim
    ) {
        Row row =
            sheet.createRow(rowIndex);

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss"
            );

        row.createCell(0)
            .setCellValue(
                nullToEmpty(
                    claim.getClaimNumber()
                )
            );

        row.createCell(1)
            .setCellValue(
                claim.getCreatedAt() != null
                    ? claim.getCreatedAt()
                        .format(formatter)
                    : ""
            );

        row.createCell(2)
            .setCellValue(
                claim.getAccidentAt() != null
                    ? claim.getAccidentAt()
                        .format(formatter)
                    : ""
            );

        row.createCell(3)
            .setCellValue(
                claim.getHotel() != null
                    ? claim.getHotel().getName()
                    : ""
            );

        row.createCell(4)
            .setCellValue(
                claim.getBranch() != null
                    ? claim.getBranch().getName()
                    : ""
            );

        row.createCell(5)
            .setCellValue(
                nullToEmpty(
                    claim.getReceivedByName()
                )
            );

        row.createCell(6)
            .setCellValue(
                PersonalDataMasker.maskName(
                    claim.getVictimName()
                )
            );

        row.createCell(7)
            .setCellValue(
                PersonalDataMasker.maskBirthDate(
                    claim.getVictimBirthDate()
                )
            );

        row.createCell(8)
            .setCellValue(
                PersonalDataMasker.maskPhone(
                    claim.getVictimPhone()
                )
            );

        row.createCell(9)
            .setCellValue(
                resolveProgressStatus(claim)
            );

        row.createCell(10)
            .setCellValue(
                claim.getAdjustingCompany() != null
                    ? claim.getAdjustingCompany().getName()
                    : ""
            );

        row.createCell(11)
            .setCellValue(
                claim.getAdjuster() != null
                    ? claim.getAdjuster().getName()
                    : ""
            );

        row.createCell(12)
            .setCellValue(
                claim.getAdjuster() != null
                    ? claim.getAdjuster().getPhone()
                    : ""
            );

        row.createCell(13)
            .setCellValue(
                claim.getBranch() != null
                    && claim.getBranch()
                        .getInsuranceCompany() != null
                    ? claim.getBranch()
                        .getInsuranceCompany()
                        .getName()
                    : ""
            );
    }

    private String resolveProgressStatus(
            Claim claim
    ) {
        return switch (claim.getStatus()) {

            case RECEIVED ->
                "접수";

            case IN_PROGRESS ->
                "진행중";

            case CANCELLED ->
                "취소";

            case CLOSED -> {
                if (claim.getClosingResult() == null) {
                    yield "종결";
                }

                yield switch (
                    claim.getClosingResult()
                ) {
                    case INSURANCE_PAID ->
                        "종결(보험금 지급)";

                    case EXEMPTED ->
                        "종결(면책)";
                };
            }
        };
    }

    private String buildSearchConditionLog(
            ClaimSearchCondition condition
    ) {
        return String.format(
            "receivedFrom=%s, receivedTo=%s, "
                + "accidentFrom=%s, accidentTo=%s, "
                + "progressStatus=%s, "
                + "receivedByName=%s, victimName=%s",
            condition.receivedFrom(),
            condition.receivedTo(),
            condition.accidentFrom(),
            condition.accidentTo(),
            condition.progressStatus(),
            condition.receivedByName(),
            condition.victimName()
        );
    }

    private String nullToEmpty(
            String value
    ) {
        return value == null
            ? ""
            : value;
    }
}
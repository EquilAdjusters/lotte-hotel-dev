package com.example.backendlotte.claim.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.organization.repository.BranchRepository;
import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.HotelCompany;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClaimAccessContextResolver {

    private final AccountRepository accountRepository;
    private final BranchRepository branchRepository;

    @Transactional(readOnly = true)
    public ClaimAccessContext resolveForCreate(
            Long accountId
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException(
                    "로그인 계정 정보가 없습니다.");
        }

        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "로그인 계정을 찾을 수 없습니다."));

        validateUsableAccount(account);
        validateCreateRole(account);

        Branch branch = account.getBranch();

        if (branch == null) {
            throw new IllegalArgumentException(
                    "사고접수 계정에 지점 소속이 설정되어 있지 않습니다.");
        }

        Hotel hotel = branch.getHotel();

        if (hotel == null) {
            throw new IllegalArgumentException(
                    "지점의 호텔 소속 정보가 없습니다.");
        }

        HotelCompany hotelCompany = hotel.getHotelCompany();

        if (hotelCompany == null) {
            throw new IllegalArgumentException(
                    "호텔의 호텔사 소속 정보가 없습니다.");
        }

        validateActiveOrganization(
                hotelCompany,
                hotel,
                branch);

        validateAccountAffiliationConsistency(
                account,
                hotelCompany,
                hotel,
                branch);

        return new ClaimAccessContext(
                account,
                hotelCompany,
                hotel,
                branch);
    }
    
    @Transactional(readOnly = true)
    public ClaimAccessContext resolveForCreate(
            Long accountId,
            Long requestedBranchId
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException(
                "로그인 계정 정보가 없습니다."
            );
        }

        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "로그인 계정을 찾을 수 없습니다."
                )
            );

        validateUsableAccount(account);

        Branch branch;

        switch (account.getRole()) {

            case BRANCH_SHARED -> {
                /*
                * 지점 공유계정은 클라이언트가 보낸 branchId를 신뢰하지 않고
                * 무조건 계정에 등록된 지점을 사용한다.
                */
                branch = account.getBranch();

                if (branch == null) {
                    throw new IllegalArgumentException(
                        "사고접수 계정에 지점 소속이 설정되어 있지 않습니다."
                    );
                }
            }

            case ADMIN1, ADMIN2, ADMIN3, ADMIN4 -> {
                /*
                * 관리자 계정(ADMIN1~4)은 특정 지점 소속이 아니므로
                * 사고 발생 지점을 직접 선택해야 한다.
                */
                if (requestedBranchId == null) {
                    throw new IllegalArgumentException(
                        "세부지점을 선택해주세요."
                    );
                }

                branch = branchRepository
                    .findById(requestedBranchId)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "선택한 지점을 찾을 수 없습니다."
                        )
                    );
            }

            default -> throw new IllegalArgumentException(
                "현재 계정은 사고를 접수할 수 없습니다."
            );
        }

        Hotel hotel = branch.getHotel();

        if (hotel == null) {
            throw new IllegalArgumentException(
                "지점의 호텔 소속 정보가 없습니다."
            );
        }

        HotelCompany hotelCompany =
            hotel.getHotelCompany();

        if (hotelCompany == null) {
            throw new IllegalArgumentException(
                "호텔의 호텔사 소속 정보가 없습니다."
            );
        }

        validateActiveOrganization(
            hotelCompany,
            hotel,
            branch
        );

        /*
        * 소속 일관성 검사는 지점 공유계정에만 필요.
        *
        * ADMIN1~4는 특정 지점을 직접 선택해 접수하는 구조이므로
        * (ADMIN3처럼 소속 호텔이 있는 경우라도) 이 검사를 적용하지 않는다.
        */
        if (account.getRole() == Role.BRANCH_SHARED) {
            validateAccountAffiliationConsistency(
                account,
                hotelCompany,
                hotel,
                branch
            );
        }

        return new ClaimAccessContext(
            account,
            hotelCompany,
            hotel,
            branch
        );
    }

    private void validateUsableAccount(
            Account account
    ) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "활성 상태인 계정만 사고를 접수할 수 있습니다."
            );
        }
    }

    private void validateCreateRole(
            Account account
    ) {
        if (account.getRole() != Role.BRANCH_SHARED) {
            throw new IllegalArgumentException(
                "현재 사고접수 화면은 지점 공유계정만 사용할 수 있습니다."
            );
        }
    }

    private void validateActiveOrganization(
            HotelCompany hotelCompany,
            Hotel hotel,
            Branch branch
    ) {
        if (!hotelCompany.isActive()) {
            throw new IllegalStateException(
                "사용 중지된 호텔사 소속 계정은 사고를 접수할 수 없습니다."
            );
        }

        if (!hotel.isActive()) {
            throw new IllegalStateException(
                "사용 중지된 호텔 소속 계정은 사고를 접수할 수 없습니다."
            );
        }

        if (!branch.isActive()) {
            throw new IllegalStateException(
                "사용 중지된 지점 계정은 사고를 접수할 수 없습니다."
            );
        }
    }

    private void validateAccountAffiliationConsistency(
            Account account,
            HotelCompany hotelCompany,
            Hotel hotel,
            Branch branch
    ) {
        if (account.getHotelCompany() == null
                || !account.getHotelCompany()
                        .getId()
                        .equals(hotelCompany.getId())) {
            throw new IllegalStateException(
                    "계정의 호텔사 소속 정보가 지점 정보와 일치하지 않습니다.");
        }

        if (account.getHotel() == null
                || !account.getHotel()
                        .getId()
                        .equals(hotel.getId())) {
            throw new IllegalStateException(
                    "계정의 호텔 소속 정보가 지점 정보와 일치하지 않습니다.");
        }

        if (!account.getBranch()
                .getId()
                .equals(branch.getId())) {
            throw new IllegalStateException(
                    "계정의 지점 소속 정보가 올바르지 않습니다.");
        }
    }
    
    @Transactional(readOnly = true)
    public ClaimSearchAccessContext resolveForSearch(
            Long accountId
    ) {
        if (accountId == null) {
            throw new IllegalArgumentException(
                "로그인 계정 정보가 없습니다."
            );
        }

        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "로그인 계정을 찾을 수 없습니다."
                )
            );

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "활성 상태인 계정만 사고현황을 조회할 수 있습니다."
            );
        }

        return new ClaimSearchAccessContext(
            account,
            account.getHotelCompany(),
            account.getHotel(),
            account.getBranch(),
            account.getBranchGroup()
        );
    }
}
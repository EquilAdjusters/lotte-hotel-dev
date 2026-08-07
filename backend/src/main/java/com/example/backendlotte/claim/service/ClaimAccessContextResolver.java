package com.example.backendlotte.claim.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public ClaimAccessContext resolveForCreate(
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

        validateUsableAccount(account);
        validateCreateRole(account);

        Branch branch = account.getBranch();

        if (branch == null) {
            throw new IllegalArgumentException(
                "사고접수 계정에 지점 소속이 설정되어 있지 않습니다."
            );
        }

        Hotel hotel = branch.getHotel();

        if (hotel == null) {
            throw new IllegalArgumentException(
                "지점의 호텔 소속 정보가 없습니다."
            );
        }

        HotelCompany hotelCompany = hotel.getHotelCompany();

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

        validateAccountAffiliationConsistency(
            account,
            hotelCompany,
            hotel,
            branch
        );

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
                "계정의 호텔사 소속 정보가 지점 정보와 일치하지 않습니다."
            );
        }

        if (account.getHotel() == null
                || !account.getHotel()
                    .getId()
                    .equals(hotel.getId())) {
            throw new IllegalStateException(
                "계정의 호텔 소속 정보가 지점 정보와 일치하지 않습니다."
            );
        }

        if (!account.getBranch()
                .getId()
                .equals(branch.getId())) {
            throw new IllegalStateException(
                "계정의 지점 소속 정보가 올바르지 않습니다."
            );
        }
    }
}
package com.example.backendlotte.claim.service;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.BranchGroup;
import com.example.backendlotte.organization.entity.HotelCompany;

public record ClaimSearchAccessContext(
    Account account,
    HotelCompany hotelCompany,
    Hotel hotel,
    Branch branch,
    BranchGroup branchGroup
) {
}
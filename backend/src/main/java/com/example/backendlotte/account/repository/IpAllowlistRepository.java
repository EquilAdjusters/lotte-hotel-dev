package com.example.backendlotte.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.account.entity.IpAllowlist;
import com.example.backendlotte.account.type.Role;

public interface IpAllowlistRepository
        extends JpaRepository<IpAllowlist, Long> {

    List<IpAllowlist> findAllByAccountIdAndActiveTrue(Long accountId);

    List<IpAllowlist> findAllByRoleAndActiveTrue(Role role);

    boolean existsByAccountIdAndIpAddressAndActiveTrue(
        Long accountId,
        String ipAddress
    );

    boolean existsByRoleAndIpAddressAndActiveTrue(
        Role role,
        String ipAddress
    );
}
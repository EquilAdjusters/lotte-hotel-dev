package com.example.backendlotte.account.entity;

import com.example.backendlotte.account.type.Role;
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
@Table(name = "ip_allowlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IpAllowlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 특정 계정에만 적용할 때 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 역할 전체에 적용할 때 사용
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Role role;

    // IPv4, IPv6 또는 CIDR 문자열 저장
    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
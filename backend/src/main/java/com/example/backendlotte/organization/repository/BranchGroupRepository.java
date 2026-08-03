package com.example.backendlotte.organization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.BranchGroup;

public interface BranchGroupRepository
        extends JpaRepository<BranchGroup, Long> {

    Optional<BranchGroup> findByName(String name);

    boolean existsByName(String name);
}
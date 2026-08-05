package com.example.backendlotte.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.organization.entity.BranchGroup;

public interface BranchGroupRepository
        extends JpaRepository<BranchGroup, Long> {

    // 전체 권역 그룹 이름순 조회
    List<BranchGroup> findAllByOrderByNameAsc();

    // 활성 권역 그룹만 이름순 조회
    List<BranchGroup> findAllByActiveTrueOrderByNameAsc();

    // 이름으로 조회
    Optional<BranchGroup> findByName(String name);

    // 동일 이름 존재 여부
    boolean existsByName(String name);

    // 수정 시 자기 자신을 제외한 이름 중복 확인
    boolean existsByNameAndIdNot(
        String name,
        Long branchGroupId
    );
}
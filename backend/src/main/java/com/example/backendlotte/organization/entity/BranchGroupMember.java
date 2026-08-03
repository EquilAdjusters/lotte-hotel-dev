package com.example.backendlotte.organization.entity;

import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "branch_group_members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_branch_group_members_group_branch",
            columnNames = {"branch_group_id", "branch_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchGroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_group_id", nullable = false)
    private BranchGroup branchGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
}
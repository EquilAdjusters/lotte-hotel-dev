package com.example.backendlotte.organization.entity;

import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "branch_group_members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_branch_group_member",
            columnNames = {
                "branch_group_id",
                "branch_id"
            }
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
    @JoinColumn(name = "branch_group_id")
    private BranchGroup branchGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private BranchGroupMember(
            BranchGroup branchGroup,
            Branch branch
    ) {
        this.branchGroup = branchGroup;
        this.branch = branch;
    }

    public static BranchGroupMember create(
            BranchGroup branchGroup,
            Branch branch
    ) {
        return new BranchGroupMember(
            branchGroup,
            branch
        );
    }
}
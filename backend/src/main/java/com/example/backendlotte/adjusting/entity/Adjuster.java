package com.example.backendlotte.adjusting.entity;

import com.example.backendlotte.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "adjusters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Adjuster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "adjusting_company_id",
        nullable = false
    )
    private AdjustingCompany adjustingCompany;

    @Column(
        nullable = false,
        length = 100
    )
    private String name;

    @Column(
        nullable = false,
        length = 30
    )
    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    private Adjuster(
            AdjustingCompany adjustingCompany,
            String name,
            String phone
    ) {
        this.adjustingCompany = adjustingCompany;
        this.name = name;
        this.phone = phone;
        this.active = true;
    }

    public static Adjuster create(
            AdjustingCompany adjustingCompany,
            String name,
            String phone
    ) {
        return new Adjuster(
            adjustingCompany,
            name,
            phone
        );
    }

    public void update(
            String name,
            String phone
    ) {
        this.name = name;
        this.phone = phone;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
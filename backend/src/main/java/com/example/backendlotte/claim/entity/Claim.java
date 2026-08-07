package com.example.backendlotte.claim.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.claim.type.ClaimClosingResult;
import com.example.backendlotte.claim.type.ClaimStatus;
import com.example.backendlotte.claim.type.ClaimType;
import com.example.backendlotte.claim.type.PreferredLanguage;
import com.example.backendlotte.claim.type.VictimType;
import com.example.backendlotte.global.entity.BaseEntity;
import com.example.backendlotte.hotel.entity.Hotel;
import com.example.backendlotte.organization.entity.Branch;
import com.example.backendlotte.organization.entity.HotelCompany;

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
@Table(name = "claims")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 우리 시스템의 공식 접수번호.
     * 예: 2608-0001
     */
    @Column(
        name = "claim_number",
        nullable = false,
        length = 30,
        unique = true
    )
    private String claimNumber;

    /*
     * 사고 접수 당시의 조직 정보를 보존한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "hotel_company_id",
        nullable = false
    )
    private HotelCompany hotelCompany;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "hotel_id",
        nullable = false
    )
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        nullable = false
    )
    private Branch branch;

    /*
     * 사고를 접수한 로그인 계정.
     * 공유계정의 실제 접수자는 receivedByName으로 별도 보존한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "created_by_account_id",
        nullable = false
    )
    private Account createdByAccount;

    /*
     * 피해자 정보
     */
    @Column(
        name = "victim_name",
        nullable = false,
        length = 100
    )
    private String victimName;

    @Column(
        name = "victim_phone",
        nullable = false,
        length = 30
    )
    private String victimPhone;

    @Column(
        name = "victim_birth_date",
        nullable = false
    )
    private LocalDate victimBirthDate;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "victim_type",
        nullable = false,
        length = 30
    )
    private VictimType victimType;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "preferred_language",
        length = 30
    )
    private PreferredLanguage preferredLanguage;

    /*
     * 거주지역
     */
    @Column(
        name = "residence_sido",
        nullable = false,
        length = 50
    )
    private String residenceSido;

    @Column(
        name = "residence_sigungu",
        nullable = false,
        length = 50
    )
    private String residenceSigungu;

    @Column(
        name = "residence_detail",
        length = 200
    )
    private String residenceDetail;

    /*
     * 사고 정보
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "claim_type",
        nullable = false,
        length = 30
    )
    private ClaimType claimType;

    @Column(
        name = "accident_at",
        nullable = false
    )
    private LocalDateTime accidentAt;

    @Column(
        name = "accident_description",
        nullable = false,
        length = 200
    )
    private String accidentDescription;

    /*
     * 공유계정의 실제 사용자 식별 정보
     */
    @Column(
        name = "received_by_name",
        nullable = false,
        length = 100
    )
    private String receivedByName;

    @Column(
        name = "received_by_extension",
        nullable = false,
        length = 30
    )
    private String receivedByExtension;

    /*
     * 업체와 무관한 우리 시스템 표준 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    private ClaimStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "closing_result",
        length = 30
    )
    private ClaimClosingResult closingResult;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    private Claim(
            String claimNumber,
            HotelCompany hotelCompany,
            Hotel hotel,
            Branch branch,
            Account createdByAccount,
            String victimName,
            String victimPhone,
            LocalDate victimBirthDate,
            VictimType victimType,
            PreferredLanguage preferredLanguage,
            String residenceSido,
            String residenceSigungu,
            String residenceDetail,
            ClaimType claimType,
            LocalDateTime accidentAt,
            String accidentDescription,
            String receivedByName,
            String receivedByExtension
    ) {
        this.claimNumber = claimNumber;
        this.hotelCompany = hotelCompany;
        this.hotel = hotel;
        this.branch = branch;
        this.createdByAccount = createdByAccount;

        this.victimName = victimName;
        this.victimPhone = victimPhone;
        this.victimBirthDate = victimBirthDate;
        this.victimType = victimType;
        this.preferredLanguage = preferredLanguage;

        this.residenceSido = residenceSido;
        this.residenceSigungu = residenceSigungu;
        this.residenceDetail = residenceDetail;

        this.claimType = claimType;
        this.accidentAt = accidentAt;
        this.accidentDescription = accidentDescription;

        this.receivedByName = receivedByName;
        this.receivedByExtension = receivedByExtension;

        this.status = ClaimStatus.RECEIVED;
        this.closingResult = null;
        this.closedAt = null;
    }

    public static Claim create(
            String claimNumber,
            HotelCompany hotelCompany,
            Hotel hotel,
            Branch branch,
            Account createdByAccount,
            String victimName,
            String victimPhone,
            LocalDate victimBirthDate,
            VictimType victimType,
            PreferredLanguage preferredLanguage,
            String residenceSido,
            String residenceSigungu,
            String residenceDetail,
            ClaimType claimType,
            LocalDateTime accidentAt,
            String accidentDescription,
            String receivedByName,
            String receivedByExtension
    ) {
        return new Claim(
            claimNumber,
            hotelCompany,
            hotel,
            branch,
            createdByAccount,
            victimName,
            victimPhone,
            victimBirthDate,
            victimType,
            preferredLanguage,
            residenceSido,
            residenceSigungu,
            residenceDetail,
            claimType,
            accidentAt,
            accidentDescription,
            receivedByName,
            receivedByExtension
        );
    }

    /**
     * 접수 후 문자발송 등 기본 후속 처리가 완료되면 진행중으로 변경한다.
     */
    public void startProcessing() {
        if (this.status != ClaimStatus.RECEIVED) {
            throw new IllegalStateException(
                "접수 상태인 사고만 진행중으로 변경할 수 있습니다."
            );
        }

        this.status = ClaimStatus.IN_PROGRESS;
    }

    /**
     * 외부 손사업체에서 전달받은 표준 종결 결과로 사고를 종결한다.
     */
    public void close(
            ClaimClosingResult closingResult,
            LocalDateTime closedAt
    ) {
        if (this.status == ClaimStatus.CLOSED) {
            throw new IllegalStateException(
                "이미 종결된 사고입니다."
            );
        }

        if (closingResult == null) {
            throw new IllegalArgumentException(
                "종결 결과는 필수입니다."
            );
        }

        if (closedAt == null) {
            throw new IllegalArgumentException(
                "종결 일시는 필수입니다."
            );
        }

        this.status = ClaimStatus.CLOSED;
        this.closingResult = closingResult;
        this.closedAt = closedAt;
    }
}
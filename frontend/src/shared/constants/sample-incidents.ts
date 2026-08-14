export type IncidentStatus = "접수" | "검토중" | "처리중" | "완료" | "반려";

export interface SampleIncident {
  id: string;
  occurredAt: string;
  location: string;
  category: string;
  severity: string;
  description: string;
  reporter: string;
  contact: string;
  status: IncidentStatus;
  createdAt: string;

  // ST-02 상세보기용 확장 필드 (사고접수 화면 항목과 대응, 정적 샘플 데이터)
  victimName?: string;
  victimBirthDate?: string;
  victimPhone?: string;
  victimNationality?: string;
  victimAddressSido?: string;
  victimAddressSigungu?: string;
  victimAddressDetail?: string;
  accidentPlace?: string;
  accidentPlaceDetail?: string;
  accidentDate?: string;
  accidentTime?: string;
  accidentType?: string;
  accidentSummary?: string;
  consentDate?: string;
  consentMethod?: string;
  consentAttachment?: string;
  reporterName?: string;
  reporterExtension?: string;
  insuranceCompany?: string;
  assignedName?: string;
  assignedPhone?: string;
}

export const sampleIncidents: SampleIncident[] = [
  {
    id: "HCM-2026-0001",
    occurredAt: "2026-07-28T21:35",
    location: "1207호 객실",
    category: "누수 하자",
    severity: "보통",
    description:
      "욕실 천장 배수구에서 물이 새어 카펫 일부가 젖음. 고객 요청으로 방 변경 진행.",
    reporter: "김지훈",
    contact: "010-1234-5678",
    status: "처리중",
    createdAt: "2026-07-28T22:10",

    victimName: "홍길동",
    victimBirthDate: "1990-01-01",
    victimPhone: "010-1234-5678",
    victimNationality: "내국인",
    victimAddressSido: "서울특별시",
    victimAddressSigungu: "강남구",
    victimAddressDetail: "테헤란로 123",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "서울점",
    accidentDate: "2026-07-28",
    accidentTime: "21:35",
    accidentType: "재물사고",
    accidentSummary: "욕실 천장 배수구에서 물이 새어 카펫 일부가 젖음.",
    consentDate: "2026-07-28",
    consentMethod: "서면",
    consentAttachment: "consent-0001.pdf",
    reporterName: "김지훈",
    reporterExtension: "02-3456-7890",
    insuranceCompany: "한화손해사정",
    assignedName: "이도현",
    assignedPhone: "010-9876-5432",
  },
  {
    id: "HCM-2026-0002",
    occurredAt: "2026-07-29T08:15",
    location: "지하 1층 주차장 B-32",
    category: "차량 접촉",
    severity: "높음",
    description:
      "발렛 파킹 중 기어 조작 오류로 조수석 도어에 스크래치 발생. 보험 접수 진행 예정.",
    reporter: "박성민",
    contact: "010-2345-6789",
    status: "검토중",
    createdAt: "2026-07-29T09:02",

    victimName: "박성민",
    victimBirthDate: "1985-05-12",
    victimPhone: "010-2345-6789",
    victimNationality: "내국인",
    victimAddressSido: "경기도",
    victimAddressSigungu: "성남시 분당구",
    victimAddressDetail: "정자로 45",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "서울점",
    accidentDate: "2026-07-29",
    accidentTime: "08:15",
    accidentType: "배상사고",
    accidentSummary: "발렛 파킹 중 기어 조작 오류로 조수석 도어에 스크래치 발생.",
    consentDate: "2026-07-29",
    consentMethod: "문자",
    reporterName: "박성민",
    reporterExtension: "02-3456-7891",
  },
  {
    id: "HCM-2026-0003",
    occurredAt: "2026-07-29T19:40",
    location: "3층 그랜드 다이닝",
    category: "서비스 불만",
    severity: "낮음",
    description: "예약 시각보다 20분 늦게 착석 안내. 웰컴 디저트 서비스로 응대 완료.",
    reporter: "이서연",
    contact: "010-3456-7890",
    status: "완료",
    createdAt: "2026-07-29T20:05",

    victimName: "이서연",
    victimBirthDate: "1993-11-02",
    victimPhone: "010-3456-7890",
    victimNationality: "내국인",
    victimAddressSido: "서울특별시",
    victimAddressSigungu: "송파구",
    victimAddressDetail: "올림픽로 300",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "부여점",
    accidentDate: "2026-07-29",
    accidentTime: "19:40",
    accidentType: "배상사고",
    accidentSummary: "예약 시각보다 20분 늦게 착석 안내.",
    consentDate: "2026-07-29",
    consentMethod: "구두",
    reporterName: "이서연",
    reporterExtension: "02-3456-7892",
    insuranceCompany: "한화손해사정",
    assignedName: "정우성",
    assignedPhone: "010-1111-2222",
  },
  {
    id: "HCM-2026-0004",
    occurredAt: "2026-07-30T02:22",
    location: "18층 스카이 라운지",
    category: "안전 민원",
    severity: "보통",
    description: "인접 객실 파티 소음으로 다른 투숙객 컴플레인 접수. 담당 매니저 현장 이동 안내.",
    reporter: "최다은",
    contact: "010-4567-8901",
    status: "접수",
    createdAt: "2026-07-30T02:35",

    victimName: "최다은",
    victimBirthDate: "1997-03-21",
    victimPhone: "010-4567-8901",
    victimNationality: "외국인",
    victimAddressSido: "서울특별시",
    victimAddressSigungu: "중구",
    victimAddressDetail: "을지로 30",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "서울점",
    accidentDate: "2026-07-30",
    accidentTime: "02:22",
    accidentType: "재물사고",
    accidentSummary: "인접 객실 파티 소음으로 다른 투숙객 컴플레인 접수.",
    consentDate: "2026-07-30",
    consentMethod: "서면",
    reporterName: "최다은",
    reporterExtension: "02-3456-7893",
  },
  {
    id: "HCM-2026-0005",
    occurredAt: "2026-07-30T10:05",
    location: "메인 로비",
    category: "분실물",
    severity: "낮음",
    description: "체크아웃 중 고객 서류 파우치 분실 신고. 청소팀 확인 요청 중.",
    reporter: "정한별",
    contact: "010-5678-9012",
    status: "검토중",
    createdAt: "2026-07-30T10:20",

    victimName: "정한별",
    victimBirthDate: "1988-09-09",
    victimPhone: "010-5678-9012",
    victimNationality: "내국인",
    victimAddressSido: "인천광역시",
    victimAddressSigungu: "연수구",
    victimAddressDetail: "송도과학로 12",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "서울점",
    accidentDate: "2026-07-30",
    accidentTime: "10:05",
    accidentType: "재물사고",
    accidentSummary: "체크아웃 중 고객 서류 파우치 분실 신고.",
    consentDate: "2026-07-30",
    consentMethod: "문자",
    reporterName: "정한별",
    reporterExtension: "02-3456-7894",
  },
  {
    id: "HCM-2026-0006",
    occurredAt: "2026-07-30T14:30",
    location: "5층 스파 & 웰니스",
    category: "고객 부상",
    severity: "긴급",
    description: "미끄러운 바닥으로 인한 낙상 사고. 하우스 닥터 검진 후 병원 이송 진행.",
    reporter: "한서우",
    contact: "010-6789-0123",
    status: "처리중",
    createdAt: "2026-07-30T14:45",

    victimName: "한서우",
    victimBirthDate: "1979-12-17",
    victimPhone: "010-6789-0123",
    victimNationality: "내국인",
    victimAddressSido: "서울특별시",
    victimAddressSigungu: "서초구",
    victimAddressDetail: "서초대로 77",
    accidentPlace: "롯데호텔",
    accidentPlaceDetail: "서울점",
    accidentDate: "2026-07-30",
    accidentTime: "14:30",
    accidentType: "배상사고",
    accidentSummary: "미끄러운 바닥으로 인한 낙상 사고. 하우스 닥터 검진 후 병원 이송 진행.",
    consentDate: "2026-07-30",
    consentMethod: "서면",
    consentAttachment: "consent-0006.pdf",
    reporterName: "한서우",
    reporterExtension: "02-3456-7895",
    insuranceCompany: "한화손해사정",
    assignedName: "김태리",
    assignedPhone: "010-3333-4444",
  },
];

export const severityBadge: Record<string, string> = {
  낮음: "bg-secondary-100 text-secondary-900 border-secondary-200",
  보통: "bg-background-200/70 text-foreground-800 border-background-300/60",
  높음: "bg-accent-100 text-accent-900 border-accent-300",
  긴급: "bg-primary-500 text-background-50 border-primary-500",
};

export const statusBadge: Record<string, string> = {
  접수: "bg-secondary-100 text-secondary-900",
  검토중: "bg-accent-100 text-accent-900",
  처리중: "bg-primary-100 text-primary-800",
  완료: "bg-background-200/80 text-foreground-700",
  반려: "bg-background-100 text-foreground-500",
};

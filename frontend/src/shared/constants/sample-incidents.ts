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

import type { IncidentStatus } from "@/shared/constants/sample-incidents";

export interface AdminAccount {
  id: string;
  category: string;
  type: "major" | "minor";
  loginId: string;
  password: string;
  insurer: string;
  email: string;
}

export const sampleAdminAccounts: AdminAccount[] = [
  {
    id: "acc-1",
    category: "호텔롯데",
    type: "major",
    loginId: "롯데",
    password: "롯데11",
    insurer: "롯데손보",
    email: "CLAIM@LOTTEINS.COM",
  },
  {
    id: "acc-2",
    category: "위탁사",
    type: "minor",
    loginId: "위탁",
    password: "위탁11",
    insurer: "롯데손보",
    email: "CLAIM@LOTTEINS.COM",
  },
  {
    id: "acc-3",
    category: "부산점",
    type: "minor",
    loginId: "부산",
    password: "부산11",
    insurer: "롯데손보",
    email: "CLAIM@LOTTEINS.COM",
  },
  {
    id: "acc-4",
    category: "부산CC",
    type: "minor",
    loginId: "부산CC",
    password: "부산CC11",
    insurer: "롯데손보",
    email: "CLAIM@LOTTEINS.COM",
  },
];

export interface InsurerCompany {
  id: string;
  category: string;
  name: string;
  bizNumber: string;
}

export const sampleInsurerCompanies: InsurerCompany[] = [
  {
    id: "ins-1",
    category: "호텔롯데",
    name: "롯데손해사정",
    bizNumber: "000-00-0000",
  },
  {
    id: "ins-2",
    category: "위탁사",
    name: "국제손해사정",
    bizNumber: "000-00-0000",
  },
  {
    id: "ins-3",
    category: "부산점",
    name: "롯데손해사정",
    bizNumber: "000-00-0000",
  },
  {
    id: "ins-4",
    category: "부산CC",
    name: "미래손사",
    bizNumber: "000-00-0000",
  },
];

export interface ClaimListItem {
  id: string;
  seq: number;
  victimName: string;
  phone: string;
  city: string;
  receivedAt: string;
  occurredAt: string;
  category: string;
  details: string;
  hotel: string;
  branch: string;
  assignedInsurer: string;
  assignedContact: string;
  insuranceCompany: string;
  insurancePhone: string;
  email: string;
  status: IncidentStatus;
}

export const insurerAssignOptions = [
  "업체 선택",
  "롯데손해사정",
  "국제손해사정",
  "미래손사",
  "동부손해사정",
];

export const sampleClaimList: ClaimListItem[] = [
  {
    id: "HCM-2026-0001",
    seq: 1,
    victimName: "황서준",
    phone: "010-7770-2558",
    city: "서울시 노원구",
    receivedAt: "2026-03-05",
    occurredAt: "2026-02-05",
    category: "배상",
    details: "출입문충돌",
    hotel: "롯데호텔",
    branch: "위탁지점",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "처리중",
  },
  {
    id: "HCM-2026-0002",
    seq: 2,
    victimName: "김민준",
    phone: "010-2345-6789",
    city: "서울시 중구",
    receivedAt: "2026-03-06",
    occurredAt: "2026-03-01",
    category: "누수 하자",
    details: "객실 가구 하자",
    hotel: "롯데호텔",
    branch: "위탁지점",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "검토중",
  },
  {
    id: "HCM-2026-0003",
    seq: 3,
    victimName: "이서연",
    phone: "010-3456-7890",
    city: "부산시 해운대구",
    receivedAt: "2026-03-07",
    occurredAt: "2026-03-05",
    category: "서비스 불만",
    details: "예약 지연 안내",
    hotel: "롯데호텔",
    branch: "부산점",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "완료",
  },
  {
    id: "HCM-2026-0004",
    seq: 4,
    victimName: "박성민",
    phone: "010-4567-8901",
    city: "서울시 중구",
    receivedAt: "2026-03-07",
    occurredAt: "2026-03-06",
    category: "차량 접촉",
    details: "주차장 기능 접촉",
    hotel: "롯데호텔",
    branch: "위탁지점",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "접수",
  },
  {
    id: "HCM-2026-0005",
    seq: 5,
    victimName: "정한별",
    phone: "010-5678-9012",
    city: "충청남도 부여군",
    receivedAt: "2026-03-08",
    occurredAt: "2026-03-07",
    category: "분실물",
    details: "체크아웃 중 고객 파우치 분실",
    hotel: "롯데호텔",
    branch: "부산CC",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "검토중",
  },
  {
    id: "HCM-2026-0006",
    seq: 6,
    victimName: "한서우",
    phone: "010-6789-0123",
    city: "서울시 중구",
    receivedAt: "2026-03-08",
    occurredAt: "2026-03-08",
    category: "고객 부상",
    details: "스파 바닥 미끄러짐 낙상",
    hotel: "롯데호텔",
    branch: "위탁지점",
    assignedInsurer: "업체 선택",
    assignedContact: "이름",
    insuranceCompany: "롯데손보",
    insurancePhone: "전화번호",
    email: "CLAIM@LOTTEINS.COM",
    status: "처리중",
  },
];

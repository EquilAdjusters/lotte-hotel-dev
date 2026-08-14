export type VictimType = "DOMESTIC" | "FOREIGNER";

export type PreferredLanguage = "KOREAN" | "ENGLISH" | "CHINESE" | "JAPANESE";

export type ClaimType = "PROPERTY_DAMAGE" | "LIABILITY";

export type ClaimStatus = "RECEIVED" | "IN_PROGRESS" | "CLOSED" | "CANCELLED";

export type ClaimClosingResult = "INSURANCE_PAID" | "EXEMPTED";

export type ClaimProgressStatus =
  | "IN_PROGRESS"
  | "CLOSED_PAID"
  | "CLOSED_EXEMPTED"
  | "CANCELLED";

export type ConsentStatus = "OBTAINED" | "NOT_OBTAINED";

export type ConsentMethod = "WRITTEN" | "TEXT_MESSAGE" | "ORAL";

export type ClaimAttachmentType =
  | "CONSENT_FORM"
  | "ACCIDENT_REPORT"
  | "ACCIDENT_PHOTO"
  | "DAMAGE_PHOTO"
  | "RECEIPT"
  | "OTHER";

export interface ClaimAttachmentResponse {
  id: number;
  claimId: number;
  attachmentType: ClaimAttachmentType;
  originalFileName: string;
  contentType: string;
  fileSize: number;
  uploadedByAccountId: number;
  createdAt: string;
}

export interface ClaimConsentPayload {
  consentStatus: ConsentStatus;
  consentObtainedAt: string | null;
  consentMethod: ConsentMethod | null;
}

export interface ClaimCreatePayload {
  victimName: string;
  victimPhone: string;
  victimBirthDate: string; // ISO date (yyyy-MM-dd)
  victimType: VictimType;
  preferredLanguage: PreferredLanguage | null;
  residenceSido: string;
  residenceSigungu: string;
  residenceDetail: string | null;
  // BRANCH_SHARED 계정은 서버가 계정 소속 지점으로 강제 고정하므로 무시된다.
  // ADMIN1~4가 접수하는 경우에만 실제로 사용되는 값.
  branchId?: number | null;
  claimType: ClaimType;
  accidentAt: string; // ISO local date-time (yyyy-MM-ddTHH:mm)
  accidentDescription: string;
  receivedByName: string;
  receivedByExtension: string;
  consent: ClaimConsentPayload;
}

export type ClaimUpdatePayload = ClaimCreatePayload;

export interface ClaimResponse {
  id: number;
  claimNumber: string;

  hotelCompanyId: number;
  hotelCompanyName: string;
  hotelId: number;
  hotelName: string;
  branchId: number;
  branchName: string;

  createdByAccountId: number;
  createdByLoginId: string;

  victimName: string;
  victimPhone: string;
  victimBirthDate: string;
  victimType: VictimType;
  preferredLanguage: PreferredLanguage | null;

  residenceSido: string;
  residenceSigungu: string;
  residenceDetail: string | null;

  claimType: ClaimType;
  accidentAt: string;
  accidentDescription: string;

  receivedByName: string;
  receivedByExtension: string;

  consentStatus: ConsentStatus;
  consentObtainedAt: string | null;
  consentMethod: ConsentMethod | null;

  status: ClaimStatus;
  closingResult: ClaimClosingResult | null;
  closedAt: string | null;

  createdAt: string;
  updatedAt: string;

  adjustingCompanyName: string | null;
  adjusterName: string | null;
  adjusterPhone: string | null;
}

export interface ClaimListResponse {
  claimId: number;
  claimNumber: string;

  receivedAt: string;
  accidentAt: string;

  receivedByName: string;
  victimName: string;
  victimBirthDate: string;

  adjustingCompanyName: string | null;
  adjusterName: string | null;
  adjusterPhone: string | null;

  progressStatus: ClaimProgressStatus;
}

export interface ClaimDuplicateResponse {
  claimId: number;
  claimNumber: string;
  victimName: string;
  accidentAt: string;
  hotelName: string;
  branchName: string;
  status: ClaimStatus;
}

export interface ClaimHistoryResponse {
  id: number;
  claimId: number;
  actorAccountId: number | null;
  actorLoginId: string | null;
  historyType:
    | "CREATED"
    | "STATUS_CHANGED"
    | "CLOSED"
    | "CONSENT_UPDATED"
    | "ASSIGNED"
    | "REASSIGNED"
    | "UPDATED"
    | "CANCELLED"
    | "ASSIGNMENT_CHANGED";
  sourceType: "USER" | "SYSTEM" | "EXTERNAL_ADAPTER";
  previousStatus: ClaimStatus | null;
  currentStatus: ClaimStatus | null;
  closingResult: ClaimClosingResult | null;
  previousValue: string | null;
  currentValue: string | null;
  description: string;
  createdAt: string;
}

export interface ClaimSearchCondition {
  receivedFrom?: string | null;
  receivedTo?: string | null;
  accidentFrom?: string | null;
  accidentTo?: string | null;
  progressStatus?: ClaimProgressStatus | null;
  receivedByName?: string | null;
  victimName?: string | null;
}

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // 0-based current page
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

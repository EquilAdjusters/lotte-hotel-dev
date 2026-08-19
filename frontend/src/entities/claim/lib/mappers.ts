import type {
  ClaimAttachmentType,
  ClaimProgressStatus,
  ClaimType,
  ConsentMethod,
  PreferredLanguage,
  VictimType,
} from "@/entities/claim/model/types";

export const nationalityLabelToVictimType: Record<string, VictimType> = {
  내국인: "DOMESTIC",
  외국인: "FOREIGNER",
};

export const claimTypeLabelToEnum: Record<string, ClaimType> = {
  재물사고: "PROPERTY_DAMAGE",
  배상사고: "LIABILITY",
};

export const consentMethodLabelToEnum: Record<string, ConsentMethod> = {
  서면: "WRITTEN",
  문자: "TEXT_MESSAGE",
  구두: "ORAL",
};

export const languageLabelToEnum: Record<string, PreferredLanguage> = {
  한국어: "KOREAN",
  영어: "ENGLISH",
  중국어: "CHINESE",
  일어: "JAPANESE",
};

export const victimTypeToLabel: Record<VictimType, string> = {
  DOMESTIC: "내국인",
  FOREIGNER: "외국인",
};

export const claimTypeToLabel: Record<ClaimType, string> = {
  PROPERTY_DAMAGE: "재물사고",
  LIABILITY: "배상사고",
};

export const consentMethodToLabel: Record<ConsentMethod, string> = {
  WRITTEN: "서면",
  TEXT_MESSAGE: "문자",
  ORAL: "구두",
};

export const languageToLabel: Record<PreferredLanguage, string> = {
  KOREAN: "한국어",
  ENGLISH: "영어",
  CHINESE: "중국어",
  JAPANESE: "일어",
};

export const progressStatusToLabel: Record<ClaimProgressStatus, string> = {
  IN_PROGRESS: "진행중",
  CLOSED_PAID: "종결(보험금 지급)",
  CLOSED_EXEMPTED: "종결(면책)",
  CANCELLED: "취소",
};

export const progressStatusBadge: Record<ClaimProgressStatus, string> = {
  IN_PROGRESS: "bg-primary-100 text-primary-800",
  CLOSED_PAID: "bg-background-200/80 text-foreground-700",
  CLOSED_EXEMPTED: "bg-background-100 text-foreground-500",
  CANCELLED: "bg-accent-100 text-accent-900",
};

export const attachmentTypeToLabel: Record<ClaimAttachmentType, string> = {
  CONSENT_FORM: "개인정보 동의서",
  ACCIDENT_REPORT: "사고경위서",
  ACCIDENT_PHOTO: "사고 현장 사진",
  DAMAGE_PHOTO: "피해 물품·손해 사진",
  RECEIPT: "영수증",
  OTHER: "기타",
};

/**
 * 주민등록번호 앞자리 관행(YYMMDD)에 따라 70 미만은 20xx년, 그 외는 19xx년으로 간주해
 * ISO 날짜 문자열(yyyy-MM-dd)로 변환한다. 6자리 숫자가 아니면 null을 반환한다.
 */
export function parseKoreanShortBirthDate(yymmdd: string): string | null {
  const trimmed = yymmdd.trim();
  if (!/^\d{6}$/.test(trimmed)) return null;

  const yy = Number(trimmed.slice(0, 2));
  const mm = trimmed.slice(2, 4);
  const dd = trimmed.slice(4, 6);
  const century = yy < 70 ? 2000 : 1900;
  const yyyy = century + yy;

  const month = Number(mm);
  const day = Number(dd);
  if (month < 1 || month > 12 || day < 1 || day > 31) return null;

  return `${yyyy}-${mm}-${dd}`;
}

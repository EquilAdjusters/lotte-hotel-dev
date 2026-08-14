import type {
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

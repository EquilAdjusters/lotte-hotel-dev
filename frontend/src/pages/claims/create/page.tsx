import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import AppShell from "@/app/layouts/AppShell";
import { useAuth } from "@/shared/hooks/useAuth";
import { sidoList, sigunguMap } from "@/shared/constants/regions";
import {
  createClaim,
  fetchDuplicateClaims,
  uploadClaimAttachment,
} from "@/entities/claim/api/claimApi";
import type { ClaimDuplicateResponse } from "@/entities/claim/model/types";
import { fetchBranches } from "@/entities/branch/api/branchApi";
import type { BranchOption } from "@/entities/branch/model/types";
import {
  claimTypeLabelToEnum,
  consentMethodLabelToEnum,
  languageLabelToEnum,
  nationalityLabelToVictimType,
  parseKoreanShortBirthDate,
} from "@/entities/claim/lib/mappers";

const languageOptions = ["영어", "중국어", "일어", "한국어"];

interface FormData {
  victimName: string;
  victimPhone: string;
  victimBirthDate: string;
  victimNationality: string;
  victimLanguages: string[];
  victimAddressSido: string;
  victimAddressSigungu: string;
  victimAddressDetail: string;
  accidentDate: string;
  accidentTime: string;
  accidentType: string;
  accidentSummary: string;
  reporterName: string;
  reporterExtension: string;
  branchId: number | null;
  consentObtained: boolean;
  consentDate: string;
  consentMethod: string;
  consentAttachment: File | null;
}

const initialForm: FormData = {
  victimName: "",
  victimPhone: "",
  victimBirthDate: "",
  victimNationality: "내국인",
  victimLanguages: ["한국어"],
  victimAddressSido: "",
  victimAddressSigungu: "",
  victimAddressDetail: "",
  accidentDate: "",
  accidentTime: "",
  accidentType: "",
  accidentSummary: "",
  reporterName: "",
  reporterExtension: "",
  branchId: null,
  consentObtained: false,
  consentDate: "",
  consentMethod: "서면",
  consentAttachment: null,
};

export default function ClaimCreatePage() {
  return (
    <AppShell>
      <ReportContent />
    </AppShell>
  );
}

function ReportContent() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isBranchShared = user?.role === "BRANCH_SHARED";

  const [form, setForm] = useState<FormData>(() => ({
    ...initialForm,
    reporterName: user?.displayName ?? "",
  }));
  const [submitting, setSubmitting] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [attachmentWarning, setAttachmentWarning] = useState<string | null>(null);

  const [branches, setBranches] = useState<BranchOption[]>([]);
  const [branchesLoading, setBranchesLoading] = useState(() => !isBranchShared);

  useEffect(() => {
    if (isBranchShared) return;
    fetchBranches()
      .then(setBranches)
      .catch(() => setBranches([]))
      .finally(() => setBranchesLoading(false));
  }, [isBranchShared]);

  // 피해자명 + 생년월일 조합으로 중복 의심 접수를 조회한다. (지점공유계정 전용, 하드 블록 아님)
  const [duplicates, setDuplicates] = useState<ClaimDuplicateResponse[]>([]);

  useEffect(() => {
    if (!isBranchShared) return;
    const timer = setTimeout(() => {
      const birthDate = parseKoreanShortBirthDate(form.victimBirthDate);
      if (!form.victimName.trim() || !birthDate) {
        setDuplicates([]);
        return;
      }
      fetchDuplicateClaims(form.victimName.trim(), birthDate)
        .then(setDuplicates)
        .catch(() => setDuplicates([]));
    }, 500);
    return () => clearTimeout(timer);
  }, [isBranchShared, form.victimName, form.victimBirthDate]);


  const update = <K extends keyof FormData>(k: K, v: FormData[K]) => {
    setForm((prev) => ({ ...prev, [k]: v }));
  };

  const todayStr = () => {
    const d = new Date();
    const pad = (n: number) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  };

  const validate = () => {
    if (!form.victimName.trim()) return "피해자명을 입력해 주세요.";
    if (!form.victimPhone.trim()) return "휴대전화번호를 입력해 주세요.";
    if (!form.victimBirthDate.trim()) return "생년월일을 입력해 주세요.";
    if (!parseKoreanShortBirthDate(form.victimBirthDate))
      return "생년월일은 YYMMDD 6자리 숫자로 입력해 주세요. (예: 990101)";
    if (!form.victimNationality) return "내·외국인 여부를 선택해 주세요.";
    if (form.victimNationality === "외국인" && form.victimLanguages.length === 0)
      return "외국인은 사용가능 언어를 하나 이상 선택해 주세요.";
    if (!form.victimAddressSido) return "거주지역(시·도)을 선택해 주세요.";
    if (!form.victimAddressSigungu) return "거주지역(시·군·구)을 선택해 주세요.";
    if (!form.victimAddressDetail.trim()) return "상세 주소를 입력해 주세요.";
    if (!isBranchShared && !form.branchId) return "세부지점을 선택해 주세요.";
    if (!form.accidentDate) return "사고일시(날짜)를 선택해 주세요.";
    if (!form.accidentTime) return "사고일시(시간)를 입력해 주세요.";
    if (!form.accidentType) return "사고유형을 선택해 주세요.";
    if (!form.accidentSummary.trim()) return "사고경위를 입력해 주세요.";
    if (form.accidentSummary.length > 200) return "사고경위는 200자 이내로 입력해 주세요.";
    if (!form.reporterName.trim()) return "접수자명을 입력해 주세요.";
    if (!form.reporterExtension.trim()) return "접수자 내선번호를 입력해 주세요.";
    if (!form.consentObtained) return "동의 취득 확인에 체크해 주세요.";
    if (!form.consentDate) return "동의 취득 일시를 입력해 주세요.";
    if (!form.consentMethod) return "동의 수단 방법을 선택해 주세요.";
    return null;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    const msg = validate();
    if (msg) {
      setError(msg);
      return;
    }

    const victimBirthDate = parseKoreanShortBirthDate(form.victimBirthDate);
    if (!victimBirthDate) {
      setError("생년월일 형식이 올바르지 않습니다.");
      return;
    }

    const isForeigner = form.victimNationality === "외국인";
    const nowTime = new Date().toISOString().slice(11, 19);

    setSubmitting(true);
    setAttachmentWarning(null);
    try {
      // BRANCH_SHARED는 계정 소속 지점으로 서버가 강제 고정하므로 branchId를 보내지 않는다.
      // ADMIN1~4는 세부지점을 직접 선택해야 하므로 branchId를 함께 전송한다.
      const response = await createClaim({
        victimName: form.victimName.trim(),
        victimPhone: form.victimPhone.trim(),
        victimBirthDate,
        victimType: nationalityLabelToVictimType[form.victimNationality],
        preferredLanguage: isForeigner
          ? languageLabelToEnum[form.victimLanguages[0]]
          : null,
        residenceSido: form.victimAddressSido,
        residenceSigungu: form.victimAddressSigungu,
        residenceDetail: form.victimAddressDetail.trim() || null,
        branchId: isBranchShared ? null : form.branchId,
        claimType: claimTypeLabelToEnum[form.accidentType],
        accidentAt: `${form.accidentDate}T${form.accidentTime}`,
        accidentDescription: form.accidentSummary.trim(),
        receivedByName: form.reporterName.trim(),
        receivedByExtension: form.reporterExtension.trim(),
        consent: {
          consentStatus: "OBTAINED",
          consentObtainedAt: `${form.consentDate}T${nowTime}`,
          consentMethod: consentMethodLabelToEnum[form.consentMethod],
        },
      });

      if (form.consentAttachment) {
        try {
          await uploadClaimAttachment(
            response.id,
            "CONSENT_FORM",
            form.consentAttachment
          );
        } catch {
          setAttachmentWarning(
            "접수는 완료되었으나 동의서 첨부파일 업로드에는 실패했습니다."
          );
        }
      }

      setShowModal(true);
      setForm({ ...initialForm, reporterName: user?.displayName ?? "" });
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const message = (err.response?.data as { message?: string } | undefined)
          ?.message;
        setError(message ?? "접수 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
      } else {
        setError("서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleReset = () => {
    setForm({ ...initialForm, reporterName: user?.displayName ?? "" });
    setError(null);
    setAttachmentWarning(null);
    setShowModal(false);
  };

  const toggleLanguage = (lang: string) => {
    const exists = form.victimLanguages.includes(lang);
    if (exists) {
      update(
        "victimLanguages",
        form.victimLanguages.filter((l) => l !== lang)
      );
    } else {
      update("victimLanguages", [...form.victimLanguages, lang]);
    }
  };

  return (
    <>
      <div className="w-full">
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* ── ① 피해자 정보 ── */}
          <section className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
            <SectionHeader number="1" title="피해자 정보" />
            <div className="p-5 md:p-6 space-y-5">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <Field label="피해자명" required>
                  <input
                    type="text"
                    value={form.victimName}
                    onChange={(e) => update("victimName", e.target.value)}
                    placeholder="예: 홍길동"
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
                <Field label="휴대전화번호" required hint="- 없이 입력">
                  <input
                    type="tel"
                    value={form.victimPhone}
                    onChange={(e) => update("victimPhone", e.target.value)}
                    placeholder="예: 01012345678"
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
              </div>

              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <Field label="생년월일" required hint="예: 990101">
                  <input
                    type="text"
                    value={form.victimBirthDate}
                    onChange={(e) => update("victimBirthDate", e.target.value)}
                    placeholder="예: 990101"
                    maxLength={6}
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
                <Field label="내·외국인" required>
                  <div className="flex items-center gap-5">
                    {["내국인", "외국인"].map((v) => (
                      <label key={v} className="flex items-center gap-2 text-sm text-foreground-700 cursor-pointer">
                        <input
                          type="radio"
                          name="nationality"
                          checked={form.victimNationality === v}
                          onChange={() => update("victimNationality", v)}
                          className="h-4 w-4 accent-primary-500 cursor-pointer"
                        />
                        {v}
                      </label>
                    ))}
                  </div>
                </Field>
              </div>

              {duplicates.length > 0 && (
                <div className="rounded-md border border-accent-300 bg-accent-50/70 px-4 py-3">
                  <p className="flex items-center gap-1.5 text-xs font-medium text-accent-900">
                    <i className="ri-error-warning-line"></i>
                    동일 이름·생년월일의 접수 이력이 있습니다. (중복 접수를 막지는 않습니다)
                  </p>
                  <ul className="mt-2 space-y-1 text-[11px] text-accent-800">
                    {duplicates.map((d) => (
                      <li key={d.claimId} className="font-mono">
                        {d.claimNumber} · {d.hotelName}/{d.branchName} ·{" "}
                        {new Date(d.accidentAt).toLocaleDateString("ko-KR")}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              <Field label="사용가능 언어" hint="복수 선택 가능">
                <div className="flex flex-wrap gap-3">
                  {languageOptions.map((lang) => (
                    <label
                      key={lang}
                      className={`flex items-center gap-2 rounded-full border px-3 py-1.5 text-sm cursor-pointer whitespace-nowrap ${
                        form.victimLanguages.includes(lang)
                          ? "border-primary-500 bg-primary-500 text-background-50"
                          : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={form.victimLanguages.includes(lang)}
                        onChange={() => toggleLanguage(lang)}
                        className="hidden"
                      />
                      {lang}
                    </label>
                  ))}
                </div>
              </Field>

              <Field label="거주지역" required>
                <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
                  <select
                    value={form.victimAddressSido}
                    onChange={(e) => {
                      update("victimAddressSido", e.target.value);
                      update("victimAddressSigungu", "");
                    }}
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400 cursor-pointer"
                  >
                    <option value="">시·도 선택</option>
                    {sidoList.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                  <select
                    value={form.victimAddressSigungu}
                    onChange={(e) => update("victimAddressSigungu", e.target.value)}
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400 cursor-pointer"
                  >
                    <option value="">시·군·구 선택</option>
                    {(sigunguMap[form.victimAddressSido] ?? []).map((g) => (
                      <option key={g} value={g}>
                        {g}
                      </option>
                    ))}
                  </select>
                  <input
                    type="text"
                    value={form.victimAddressDetail}
                    onChange={(e) => update("victimAddressDetail", e.target.value)}
                    placeholder="상세주소 입력"
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </div>
                <p className="mt-1.5 text-[11px] text-foreground-500">
                  ※ 거주지역을 기준으로 지근거리 손해사정사가 배정됩니다.
                </p>
              </Field>
            </div>
          </section>

          {/* ── ② 사고 정보 ── */}
          <section className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
            <SectionHeader number="2" title="사고 정보" />
            <div className="p-5 md:p-6 space-y-5">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <Field label="피해장소" hint="고정값">
                  <div className="flex items-center gap-2 rounded-md border border-background-300/60 bg-background-100 px-3 py-2.5 text-sm text-foreground-700">
                    <i className="ri-lock-line text-foreground-400"></i>
                    롯데호텔
                  </div>
                </Field>
                {isBranchShared ? (
                  <Field label="세부지점" hint="로그인 계정 기준 자동 설정">
                    <div className="flex items-center gap-2 rounded-md border border-background-300/60 bg-background-100 px-3 py-2.5 text-sm text-foreground-700">
                      <i className="ri-lock-line text-foreground-400"></i>
                      {user?.branchName ?? "-"}
                    </div>
                  </Field>
                ) : (
                  <Field label="세부지점" required>
                    <select
                      value={form.branchId ?? ""}
                      onChange={(e) =>
                        update(
                          "branchId",
                          e.target.value ? Number(e.target.value) : null
                        )
                      }
                      disabled={branchesLoading}
                      className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400 cursor-pointer disabled:opacity-60"
                    >
                      <option value="">
                        {branchesLoading ? "지점 목록 불러오는 중..." : "세부지점 선택"}
                      </option>
                      {branches.map((b) => (
                        <option key={b.id} value={b.id}>
                          {b.name} ({b.hotelName})
                        </option>
                      ))}
                    </select>
                  </Field>
                )}
              </div>

              <Field label="사고일시" required>
                <div className="flex flex-col sm:flex-row gap-2">
                  <input
                    type="date"
                    value={form.accidentDate}
                    onChange={(e) => update("accidentDate", e.target.value)}
                    className="w-full sm:w-auto flex-1 rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                  <input
                    type="time"
                    value={form.accidentTime}
                    onChange={(e) => update("accidentTime", e.target.value)}
                    className="w-full sm:w-auto flex-1 rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                  <button
                    type="button"
                    onClick={() => update("accidentDate", todayStr())}
                    className="w-full sm:w-auto rounded-md border border-secondary-300 bg-secondary-50 px-4 py-2.5 text-sm text-secondary-700 hover:bg-secondary-100 cursor-pointer whitespace-nowrap"
                  >
                    오늘
                  </button>
                </div>
              </Field>

              <Field label="사고유형" required>
                <div className="flex flex-wrap items-center gap-5">
                  {["재물사고", "배상사고"].map((t) => (
                    <label key={t} className="flex items-center gap-2 text-sm text-foreground-700 cursor-pointer">
                      <input
                        type="radio"
                        name="accidentType"
                        checked={form.accidentType === t}
                        onChange={() => update("accidentType", t)}
                        className="h-4 w-4 accent-primary-500 cursor-pointer"
                      />
                      {t}
                      {t === "배상사고" && (
                        <span className="text-xs text-foreground-500">(구내치료비 포함)</span>
                      )}
                    </label>
                  ))}
                </div>
              </Field>

              <Field
                label="사고경위"
                required
                hint={`${form.accidentSummary.length} / 200자`}
              >
                <textarea
                  value={form.accidentSummary}
                  onChange={(e) => update("accidentSummary", e.target.value)}
                  rows={4}
                  maxLength={200}
                  placeholder="간략히 기재 부탁드립니다. (200자 이내)"
                  className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-3 text-sm outline-none focus:border-primary-400"
                />
              </Field>

              <div className="rounded-md border border-accent-200 bg-accent-50/60 px-4 py-3">
                <p className="text-xs text-accent-900">
                  ※ 진단명·부상 부위 등 상세 의료정보는 입력하지 마십시오.
                </p>
              </div>
            </div>
          </section>

          {/* ── ③ 접수자 정보 ── */}
          <section className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
            <SectionHeader number="3" title="접수자 정보" />
            <div className="p-5 md:p-6 space-y-5">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <Field label="접수자명" required>
                  <input
                    type="text"
                    value={form.reporterName}
                    onChange={(e) => update("reporterName", e.target.value)}
                    placeholder="예: 김민준"
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
                <Field label="접수자 내선번호" required hint="지역번호">
                  <input
                    type="text"
                    value={form.reporterExtension}
                    onChange={(e) => update("reporterExtension", e.target.value)}
                    placeholder="예: 02-3456-7890"
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
              </div>
            </div>
          </section>

          {/* ── ④ 동의 취득 확인 ── */}
          <section className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
            <SectionHeader number="4" title="동의 취득 확인" badge="법무근거" />
            <div className="p-5 md:p-6 space-y-5">
              <div className="rounded-md border border-secondary-200 bg-secondary-50/60 px-4 py-3">
                <label className="flex items-start gap-3 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={form.consentObtained}
                    onChange={(e) => update("consentObtained", e.target.checked)}
                    className="mt-0.5 h-4 w-4 accent-primary-500 cursor-pointer"
                  />
                  <span className="text-sm leading-relaxed text-foreground-800">
                    피해자로부터 개인정보 수집·이용, 제3자 제공, 민감정보 처리에 대한 동의를 취득하였습니다. (필수)
                  </span>
                </label>
              </div>

              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <Field label="동의 취득 일시" required>
                  <input
                    type="date"
                    value={form.consentDate}
                    onChange={(e) => update("consentDate", e.target.value)}
                    className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
                  />
                </Field>
                <Field label="동의 수단 방법" required>
                  <div className="flex items-center gap-5">
                    {["서면", "문자", "구두"].map((m) => (
                      <label key={m} className="flex items-center gap-2 text-sm text-foreground-700 cursor-pointer">
                        <input
                          type="radio"
                          name="consentMethod"
                          checked={form.consentMethod === m}
                          onChange={() => update("consentMethod", m)}
                          className="h-4 w-4 accent-primary-500 cursor-pointer"
                        />
                        {m}
                      </label>
                    ))}
                  </div>
                </Field>
              </div>

              <div>
                <div className="mb-1.5 flex items-center justify-between">
                  <label className="text-xs font-medium text-foreground-700">동의서 첨부</label>
                </div>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={form.consentAttachment?.name ?? ""}
                    readOnly
                    placeholder="선택된 파일 없음"
                    className="flex-1 rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm text-foreground-500 outline-none"
                  />
                  <label className="rounded-md border border-secondary-300 bg-secondary-50 px-4 py-2.5 text-sm text-secondary-700 hover:bg-secondary-100 cursor-pointer whitespace-nowrap">
                    첨부
                    <input
                      type="file"
                      className="hidden"
                      onChange={(e) => {
                        const file = e.target.files?.[0];
                        if (file) {
                          update("consentAttachment", file);
                        }
                      }}
                    />
                  </label>
                </div>
                <p className="mt-1.5 text-[11px] text-foreground-500">
                  ※ 사본 첨부는 선택, 취득 일시·방법은 필수입니다.
                </p>
              </div>
            </div>
          </section>

          {/* ── 알림 / 오류 ── */}
          {error && (
            <div className="flex items-center gap-2 rounded-md border border-accent-300 bg-accent-100/70 px-3 py-2 text-sm text-accent-900">
              <span className="w-5 h-5 flex items-center justify-center">
                <i className="ri-error-warning-line"></i>
              </span>
              <span>{error}</span>
            </div>
          )}

          {/* ── 하단 안내 + 버튼 ── */}
          <div className="rounded-md border border-background-200/70 bg-background-100 px-4 py-3">
            <p className="text-xs text-foreground-600">
              ※ 체크박스 미체크 시 제출 불가. 이 기준에 따른 통합 동의 취득 확인을 받고 처리했다는 효과가 됩니다.
            </p>
          </div>

          <div className="flex flex-row-reverse items-center justify-between gap-3">
            <button
              type="submit"
              disabled={submitting}
              className="rounded-md bg-primary-500 px-10 py-3 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
            >
              {submitting ? "접수 중..." : "사고접수하기"}
            </button>
            <button
              type="button"
              onClick={handleReset}
              className="rounded-md border border-background-300/60 px-6 py-3 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
            >
              초기화
            </button>
          </div>
        </form>
      </div>

      {/* ── 접수 완료 모달 ── */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-lg border border-background-200 bg-background-50 p-8 text-center shadow-lg">
            <div className="mb-6 text-sm text-foreground-900 leading-relaxed">
              <p>사고접수가 완료되었으며,</p>
              <p>피해자에게 접수문자 안내되었습니다.</p>
              {attachmentWarning && (
                <p className="mt-3 text-xs text-accent-700">{attachmentWarning}</p>
              )}
            </div>
            <button
              type="button"
              onClick={() => {
                setShowModal(false);
                navigate("/claims");
              }}
              className="w-full rounded-md bg-primary-500 py-2.5 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
            >
              확인
            </button>
          </div>
        </div>
      )}
    </>
  );
}

/* ─── sub components ─── */

function SectionHeader({
  number,
  title,
  badge,
}: {
  number: string;
  title: string;
  badge?: string;
}) {
  return (
    <div className="flex items-center gap-3 bg-primary-500 px-5 py-3">
      <span className="w-7 h-7 flex items-center justify-center rounded-full bg-background-50/20 text-xs font-semibold text-background-50">
        {number}
      </span>
      <span className="text-sm font-medium text-background-50">{title}</span>
      {badge && (
        <span className="ml-auto rounded-full bg-background-50/20 px-2.5 py-0.5 text-[11px] text-background-50/90">
          {badge}
        </span>
      )}
    </div>
  );
}

function Field({
  label,
  required,
  hint,
  children,
}: {
  label: string;
  required?: boolean;
  hint?: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <div className="mb-1.5 flex items-center justify-between">
        <label className="text-xs font-medium text-foreground-700">
          {label}
          {required && <span className="ml-1 text-accent-700">*</span>}
        </label>
        {hint && <span className="text-[11px] text-foreground-500">{hint}</span>}
      </div>
      {children}
    </div>
  );
}

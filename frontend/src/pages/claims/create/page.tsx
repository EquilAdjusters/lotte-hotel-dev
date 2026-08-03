import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AppShell from "@/app/layouts/AppShell";

const categories = [
  "누수 하자",
  "청결 문제",
  "서비스 불만",
  "안전 민원",
  "분실물",
  "차량 접촉",
  "고객 부상",
  "기타",
];

const severities = ["낮음", "보통", "높음", "긴급"];

const locations = [
  "메인 로비",
  "프론트 데스크",
  "지하 주차장",
  "그랜드 다이닝 레스토랑",
  "스파 & 웰니스 센터",
  "비즈니스 라운지",
  "객실 (직접 입력)",
];

export default function ClaimCreatePage() {
  return (
    <AppShell>
      <ClaimCreateForm />
    </AppShell>
  );
}

function nowLocal() {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}`;
}

function ClaimCreateForm() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    occurredAt: nowLocal(),
    location: "",
    customLocation: "",
    category: "",
    severity: "보통",
    description: "",
    reporter: "",
    contact: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const update = <K extends keyof typeof form>(k: K, v: (typeof form)[K]) => {
    setForm((prev) => ({ ...prev, [k]: v }));
  };

  const validate = () => {
    if (!form.occurredAt) return "발생 일시를 선택해 주세요.";
    if (!form.location) return "발생 장소를 선택해 주세요.";
    if (form.location === "객실 (직접 입력)" && !form.customLocation.trim())
      return "객실 번호 또는 세부 위치를 입력해 주세요.";
    if (!form.category) return "사고 유형을 선택해 주세요.";
    if (!form.description.trim() || form.description.trim().length < 10)
      return "상세 내용을 10자 이상 작성해 주세요.";
    if (form.description.length > 500)
      return "상세 내용은 500자를 초과할 수 없습니다.";
    if (!form.reporter.trim()) return "접수자 이름을 입력해 주세요.";
    if (!form.contact.trim()) return "연락처를 입력해 주세요.";
    return null;
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    const msg = validate();
    if (msg) {
      setError(msg);
      return;
    }
    setSubmitting(true);
    setTimeout(() => {
      setSubmitting(false);
      setSuccess(`HCM-${new Date().getFullYear()}-0007`);
    }, 500);
  };

  return (
    <div className="grid grid-cols-1 gap-8 lg:grid-cols-[1.6fr_1fr]">
      <form
        onSubmit={handleSubmit}
        className="rounded-lg border border-background-200/70 bg-background-50 p-6 md:p-8"
      >
        <div className="mb-6">
          <div className="text-xs tracking-[0.18em] uppercase text-foreground-500">
            New Report
          </div>
          <h2 className="mt-1 font-heading text-xl font-semibold">
            사고 · 클레임 접수
          </h2>
          <p className="mt-1 text-sm text-foreground-600">
            발생 정보를 정확히 기록해 주세요. 제출 즉시 클레임 번호가
            발급됩니다.
          </p>
        </div>

        <div className="space-y-5">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <Field label="발생 일시" required>
              <input
                type="datetime-local"
                value={form.occurredAt}
                onChange={(e) => update("occurredAt", e.target.value)}
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              />
            </Field>
            <Field label="심각도" required>
              <div className="flex flex-wrap gap-2">
                {severities.map((s) => (
                  <button
                    type="button"
                    key={s}
                    onClick={() => update("severity", s)}
                    className={`rounded-full border px-3 py-1.5 text-sm cursor-pointer whitespace-nowrap ${
                      form.severity === s
                        ? "border-primary-500 bg-primary-500 text-background-50"
                        : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
                    }`}
                  >
                    {s}
                  </button>
                ))}
              </div>
            </Field>
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <Field label="발생 장소" required>
              <select
                value={form.location}
                onChange={(e) => update("location", e.target.value)}
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              >
                <option value="">장소를 선택해 주세요</option>
                {locations.map((l) => (
                  <option key={l} value={l}>
                    {l}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="사고 유형" required>
              <select
                value={form.category}
                onChange={(e) => update("category", e.target.value)}
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              >
                <option value="">유형을 선택해 주세요</option>
                {categories.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </Field>
          </div>

          {form.location === "객실 (직접 입력)" && (
            <Field label="세부 위치" required>
              <input
                type="text"
                value={form.customLocation}
                onChange={(e) => update("customLocation", e.target.value)}
                placeholder="예: 1207호 객실 욕실"
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              />
            </Field>
          )}

          <Field
            label="상세 내용"
            required
            hint={`${form.description.length} / 500자`}
          >
            <textarea
              value={form.description}
              onChange={(e) => update("description", e.target.value)}
              rows={6}
              maxLength={500}
              placeholder="어떤 상황이 발생했는지 육하원칙에 맞게 기록해 주세요."
              className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-3 text-sm outline-none focus:border-primary-400"
            />
          </Field>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <Field label="접수자" required>
              <input
                type="text"
                value={form.reporter}
                onChange={(e) => update("reporter", e.target.value)}
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              />
            </Field>
            <Field label="연락처" required hint="내부 연락처 또는 고객 연락처">
              <input
                type="tel"
                value={form.contact}
                onChange={(e) => update("contact", e.target.value)}
                placeholder="예: 010-1234-5678"
                className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400"
              />
            </Field>
          </div>

          {error && (
            <div className="flex items-center gap-2 rounded-md border border-accent-300 bg-accent-100/70 px-3 py-2 text-sm text-accent-900">
              <i className="ri-error-warning-line"></i>
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="rounded-md border border-primary-200 bg-primary-50 p-4">
              <div className="flex items-start gap-3">
                <span className="w-8 h-8 flex items-center justify-center rounded-md bg-primary-500 text-background-50">
                  <i className="ri-check-line"></i>
                </span>
                <div className="flex-1">
                  <div className="text-sm font-medium text-primary-900">
                    접수가 완료되었습니다.
                  </div>
                  <div className="mt-1 text-xs text-primary-800">
                    클레임 번호:{" "}
                    <span className="font-mono font-semibold">{success}</span>
                  </div>
                  <div className="mt-3 flex gap-2">
                    <button
                      type="button"
                      onClick={() => navigate("/claims")}
                      className="rounded-md bg-primary-500 px-3 py-1.5 text-xs text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
                    >
                      현황 조회 이동
                    </button>
                    <button
                      type="button"
                      onClick={() => setSuccess(null)}
                      className="rounded-md border border-primary-300 px-3 py-1.5 text-xs text-primary-800 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
                    >
                      추가 접수
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          <div className="flex flex-col-reverse gap-3 border-t border-background-200/70 pt-5 sm:flex-row sm:justify-end">
            <button
              type="button"
              onClick={() =>
                setForm({
                  occurredAt: nowLocal(),
                  location: "",
                  customLocation: "",
                  category: "",
                  severity: "보통",
                  description: "",
                  reporter: "",
                  contact: "",
                })
              }
              className="rounded-md border border-background-300/60 px-4 py-2.5 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
            >
              초기화
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="rounded-md bg-primary-500 px-6 py-2.5 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
            >
              {submitting ? "접수 중..." : "클레임 접수하기"}
            </button>
          </div>
        </div>
      </form>

      <aside className="space-y-4">
        <div className="rounded-lg border border-background-200/70 bg-background-100 p-6">
          <div className="text-xs tracking-[0.2em] uppercase text-foreground-500">
            Reporting Tips
          </div>
          <h3 className="mt-2 font-heading text-lg font-semibold">
            접수 시 유의사항
          </h3>
          <ul className="mt-4 space-y-3 text-sm text-foreground-700">
            {[
              "고객 개인정보(카드번호, 여권번호 등)는 기록하지 않습니다.",
              "긴급·높음 심각도는 WISE 담당자에게 즉시 전화로 통보해 주세요.",
              "현장 사진은 별도 내부 메신저로 WISE 담당자에게 전달합니다.",
              "동일 사안은 하나의 클레임 번호로 통합 관리합니다.",
            ].map((t) => (
              <li key={t} className="flex gap-2">
                <span className="mt-1 w-1.5 h-1.5 flex items-center justify-center rounded-full bg-accent-500"></span>
                <span>{t}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="rounded-lg border border-background-200/70 bg-background-50 p-6">
          <div className="text-xs tracking-[0.2em] uppercase text-foreground-500">
            Contact
          </div>
          <h3 className="mt-2 font-heading text-lg font-semibold">
            WISE 긴급 연락망
          </h3>
          <ul className="mt-4 space-y-3 text-sm">
            <li className="flex items-center justify-between">
              <span className="text-foreground-700">WISE 심해사업팀</span>
              <span className="font-mono text-foreground-900">02-3456-7800</span>
            </li>
            <li className="flex items-center justify-between">
              <span className="text-foreground-700">호텔롯데 비상 매니저</span>
              <span className="font-mono text-foreground-900">#7001</span>
            </li>
            <li className="flex items-center justify-between">
              <span className="text-foreground-700">보안팀</span>
              <span className="font-mono text-foreground-900">#7010</span>
            </li>
            <li className="flex items-center justify-between">
              <span className="text-foreground-700">시설·엔지니어링</span>
              <span className="font-mono text-foreground-900">#7020</span>
            </li>
            <li className="flex items-center justify-between">
              <span className="text-foreground-700">하우스 닥터</span>
              <span className="font-mono text-foreground-900">#7099</span>
            </li>
          </ul>
        </div>
      </aside>
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

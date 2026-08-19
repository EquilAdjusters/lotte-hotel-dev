import { useEffect, useState } from "react";
import axios from "axios";
import FormField from "@/shared/components/FormField";
import {
  activateInsuranceCompany,
  createInsuranceCompany,
  deactivateInsuranceCompany,
  fetchBranchInsuranceSetting,
  fetchInsuranceCompanies,
  updateBranchInsuranceSetting,
  updateInsuranceCompany,
} from "@/entities/insurance-company/api/insuranceCompanyApi";
import type { InsuranceCompanyOption } from "@/entities/insurance-company/model/types";
import { fetchHotelCompanies } from "@/entities/hotel-company/api/hotelCompanyApi";
import type { HotelCompanyOption } from "@/entities/hotel-company/model/types";
import { fetchHotels } from "@/entities/hotel/api/hotelApi";
import type { HotelOption } from "@/entities/hotel/model/types";
import { fetchBranches } from "@/entities/branch/api/branchApi";
import type { BranchOption } from "@/entities/branch/model/types";

function errorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const message = (err.response?.data as { message?: string } | undefined)?.message;
    if (message) return message;
  }
  return fallback;
}

export default function InsuranceCompanySection() {
  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">보험사 관리</h2>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 divide-y lg:divide-y-0 lg:divide-x divide-background-200/70">
        <CompanyListPanel />
        <BranchSettingPanel />
      </div>
    </div>
  );
}

function CompanyListPanel() {
  const [companies, setCompanies] = useState<InsuranceCompanyOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [form, setForm] = useState<InsuranceCompanyOption | "new" | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const reload = () => setReloadToken((t) => t + 1);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setLoading(true);
      setError(null);
    });

    fetchInsuranceCompanies(false)
      .then((data) => {
        if (!cancelled) setCompanies(data);
      })
      .catch(() => {
        if (!cancelled) setError("보험사 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [reloadToken]);

  const handleToggle = async (c: InsuranceCompanyOption) => {
    if (c.active && !confirm(`${c.name} 보험사를 사용 중지하시겠습니까?`)) return;
    setBusyId(c.id);
    try {
      if (c.active) {
        await deactivateInsuranceCompany(c.id);
      } else {
        await activateInsuranceCompany(c.id);
      }
      reload();
    } catch (err) {
      alert(errorMessage(err, "처리 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="p-4">
      <div className="mb-2 flex items-center justify-between">
        <h3 className="text-sm font-semibold text-foreground-800">보험사 목록</h3>
        <button
          onClick={() => setForm("new")}
          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          추가
        </button>
      </div>

      {loading && <div className="py-6 text-center text-xs text-foreground-500">불러오는 중...</div>}
      {!loading && error && <div className="py-6 text-center text-xs text-accent-700">{error}</div>}
      {!loading && !error && companies.length === 0 && (
        <div className="py-6 text-center text-xs text-foreground-500">등록된 보험사가 없습니다.</div>
      )}
      {!loading && !error && (
        <ul className="space-y-1">
          {companies.map((c) => (
            <li
              key={c.id}
              className="flex items-center justify-between rounded px-2 py-1.5 text-sm hover:bg-background-100"
            >
              <span className={c.active ? "" : "text-foreground-400 line-through"}>
                {c.name}
              </span>
              <div className="flex gap-1">
                <button
                  onClick={() => setForm(c)}
                  className="text-[11px] text-foreground-500 hover:text-primary-600 cursor-pointer"
                >
                  수정
                </button>
                <button
                  onClick={() => handleToggle(c)}
                  disabled={busyId === c.id}
                  className="text-[11px] text-foreground-500 hover:text-primary-600 disabled:opacity-50 cursor-pointer"
                >
                  {c.active ? "중지" : "재활성화"}
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}

      {form && (
        <InsuranceCompanyFormModal
          target={form}
          onClose={() => setForm(null)}
          onSaved={() => {
            setForm(null);
            reload();
          }}
        />
      )}
    </div>
  );
}

function InsuranceCompanyFormModal({
  target,
  onClose,
  onSaved,
}: {
  target: InsuranceCompanyOption | "new";
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;
  const [name, setName] = useState(existing?.name ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("보험사명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        await createInsuranceCompany(name.trim());
      } else if (existing) {
        await updateInsuranceCompany(existing.id, name.trim());
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "저장 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-background-50 p-6">
        <h3 className="font-heading text-lg font-semibold">
          {isNew ? "보험사 추가" : `보험사 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4">
          <FormField label="보험사명">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
        </div>
        {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
        <div className="mt-6 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            disabled={submitting}
            className="rounded-md border border-background-300/60 px-4 py-2 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={submitting}
            className="rounded-md bg-primary-500 px-4 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
          >
            {submitting ? "저장 중..." : "저장하기"}
          </button>
        </div>
      </div>
    </div>
  );
}

function BranchSettingPanel() {
  const [hotelCompanies, setHotelCompanies] = useState<HotelCompanyOption[]>([]);
  const [hotels, setHotels] = useState<HotelOption[]>([]);
  const [branches, setBranches] = useState<BranchOption[]>([]);
  const [hotelCompanyId, setHotelCompanyId] = useState<number | "">("");
  const [hotelId, setHotelId] = useState<number | "">("");
  const [branchId, setBranchId] = useState<number | "">("");

  const [insuranceCompanies, setInsuranceCompanies] = useState<InsuranceCompanyOption[]>(
    []
  );
  const [settingLoading, setSettingLoading] = useState(false);
  const [settingError, setSettingError] = useState<string | null>(null);
  const [insuranceCompanyId, setInsuranceCompanyId] = useState<number | "">("");
  const [receiptEmail, setReceiptEmail] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    fetchHotelCompanies().then(setHotelCompanies).catch(() => setHotelCompanies([]));
    fetchInsuranceCompanies().then(setInsuranceCompanies).catch(() => setInsuranceCompanies([]));
  }, []);

  useEffect(() => {
    if (hotelCompanyId === "") return;
    fetchHotels(hotelCompanyId)
      .then(setHotels)
      .catch(() => setHotels([]));
  }, [hotelCompanyId]);

  useEffect(() => {
    if (hotelId === "") return;
    fetchBranches(hotelId)
      .then(setBranches)
      .catch(() => setBranches([]));
  }, [hotelId]);

  useEffect(() => {
    if (branchId === "") return;
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setSettingLoading(true);
      setSettingError(null);
      setSaved(false);
    });

    fetchBranchInsuranceSetting(branchId)
      .then((data) => {
        if (cancelled) return;
        setInsuranceCompanyId(data.insuranceCompanyId ?? "");
        setReceiptEmail(data.receiptEmail ?? "");
      })
      .catch(() => {
        if (!cancelled) setSettingError("설정 정보를 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setSettingLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [branchId]);

  const handleSave = async () => {
    if (branchId === "" || insuranceCompanyId === "" || !receiptEmail.trim()) {
      setSaveError("보험사와 접수메일을 모두 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setSaveError(null);
    try {
      await updateBranchInsuranceSetting(branchId, insuranceCompanyId, receiptEmail.trim());
      setSaved(true);
    } catch (err) {
      setSaveError(errorMessage(err, "저장 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-4">
      <h3 className="mb-3 text-sm font-semibold text-foreground-800">지점별 보험사·접수메일 설정</h3>

      <div className="space-y-3">
        <FormField label="호텔사">
          <select
            value={hotelCompanyId}
            onChange={(e) => {
              const next = e.target.value ? Number(e.target.value) : "";
              setHotelCompanyId(next);
              setHotelId("");
              setBranchId("");
              if (next === "") setHotels([]);
            }}
            className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
          >
            <option value="">선택</option>
            {hotelCompanies.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </FormField>
        <FormField label="호텔">
          <select
            value={hotelId}
            onChange={(e) => {
              const next = e.target.value ? Number(e.target.value) : "";
              setHotelId(next);
              setBranchId("");
              if (next === "") setBranches([]);
            }}
            disabled={hotelCompanyId === ""}
            className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer disabled:opacity-50"
          >
            <option value="">선택</option>
            {hotels.map((h) => (
              <option key={h.id} value={h.id}>
                {h.name}
              </option>
            ))}
          </select>
        </FormField>
        <FormField label="지점">
          <select
            value={branchId}
            onChange={(e) => setBranchId(e.target.value ? Number(e.target.value) : "")}
            disabled={hotelId === ""}
            className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer disabled:opacity-50"
          >
            <option value="">선택</option>
            {branches.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        </FormField>
      </div>

      {branchId !== "" && (
        <div className="mt-4 border-t border-background-200/70 pt-4">
          {settingLoading && (
            <div className="py-4 text-center text-xs text-foreground-500">불러오는 중...</div>
          )}
          {!settingLoading && settingError && (
            <div className="py-4 text-center text-xs text-accent-700">{settingError}</div>
          )}
          {!settingLoading && !settingError && (
            <div className="space-y-3">
              <FormField label="보험사">
                <select
                  value={insuranceCompanyId}
                  onChange={(e) =>
                    setInsuranceCompanyId(e.target.value ? Number(e.target.value) : "")
                  }
                  className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
                >
                  <option value="">선택</option>
                  {insuranceCompanies.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </FormField>
              <FormField label="접수메일">
                <input
                  type="email"
                  value={receiptEmail}
                  onChange={(e) => setReceiptEmail(e.target.value)}
                  className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
                />
              </FormField>
              {saveError && <p className="text-xs text-accent-700">{saveError}</p>}
              {saved && <p className="text-xs text-primary-700">저장되었습니다.</p>}
              <button
                type="button"
                onClick={handleSave}
                disabled={submitting}
                className="w-full rounded-md bg-primary-500 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
              >
                {submitting ? "저장 중..." : "저장하기"}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

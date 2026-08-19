import { useEffect, useState } from "react";
import axios from "axios";
import FormField from "@/shared/components/FormField";
import {
  createHotelCompany,
  deactivateHotelCompany,
  fetchHotelCompanies,
  updateHotelCompany,
} from "@/entities/hotel-company/api/hotelCompanyApi";
import type { HotelCompanyOption } from "@/entities/hotel-company/model/types";
import {
  activateHotel,
  createHotel,
  deactivateHotel,
  fetchHotels,
  updateHotel,
} from "@/entities/hotel/api/hotelApi";
import type { HotelOption } from "@/entities/hotel/model/types";
import {
  activateBranch,
  createBranch,
  deactivateBranch,
  fetchBranches,
  updateBranch,
} from "@/entities/branch/api/branchApi";
import type { BranchOption } from "@/entities/branch/model/types";

function errorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const message = (err.response?.data as { message?: string } | undefined)?.message;
    if (message) return message;
  }
  return fallback;
}

export default function OrganizationSection() {
  const [companies, setCompanies] = useState<HotelCompanyOption[]>([]);
  const [companiesLoading, setCompaniesLoading] = useState(true);
  const [companiesError, setCompaniesError] = useState<string | null>(null);
  const [companiesReload, setCompaniesReload] = useState(0);
  const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
  const selectedCompany =
    companies.find((c) => c.id === selectedCompanyId) ?? null;

  const [companyForm, setCompanyForm] = useState<HotelCompanyOption | "new" | null>(
    null
  );

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setCompaniesLoading(true);
      setCompaniesError(null);
    });

    fetchHotelCompanies(false)
      .then((data) => {
        if (!cancelled) setCompanies(data);
      })
      .catch(() => {
        if (!cancelled) setCompaniesError("호텔사 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setCompaniesLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [companiesReload]);

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">조직 관리 (호텔사·호텔·지점)</h2>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 divide-y lg:divide-y-0 lg:divide-x divide-background-200/70">
        {/* 호텔사 */}
        <div className="p-4">
          <div className="mb-2 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground-800">호텔사</h3>
            <button
              onClick={() => setCompanyForm("new")}
              className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
            >
              추가
            </button>
          </div>
          {companiesLoading && (
            <div className="py-6 text-center text-xs text-foreground-500">불러오는 중...</div>
          )}
          {!companiesLoading && companiesError && (
            <div className="py-6 text-center text-xs text-accent-700">{companiesError}</div>
          )}
          {!companiesLoading && !companiesError && companies.length === 0 && (
            <div className="py-6 text-center text-xs text-foreground-500">등록된 호텔사가 없습니다.</div>
          )}
          <ul className="space-y-1">
            {companies.map((c) => (
              <li
                key={c.id}
                onClick={() => setSelectedCompanyId(c.id)}
                className={`flex items-center justify-between rounded px-2 py-1.5 text-sm cursor-pointer ${
                  selectedCompany?.id === c.id
                    ? "bg-primary-50 text-primary-800"
                    : "hover:bg-background-100"
                }`}
              >
                <span className={c.active ? "" : "text-foreground-400 line-through"}>
                  {c.name}
                </span>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setCompanyForm(c);
                  }}
                  className="text-[11px] text-foreground-500 hover:text-primary-600 cursor-pointer"
                >
                  수정
                </button>
              </li>
            ))}
          </ul>
        </div>

        {/* 호텔 */}
        <HotelColumn company={selectedCompany} />

        {/* 지점은 HotelColumn 내부에서 선택된 호텔에 따라 렌더링 */}
      </div>

      {companyForm && (
        <HotelCompanyFormModal
          target={companyForm}
          onClose={() => setCompanyForm(null)}
          onSaved={() => {
            setCompanyForm(null);
            setCompaniesReload((t) => t + 1);
          }}
        />
      )}
    </div>
  );
}

function HotelCompanyFormModal({
  target,
  onClose,
  onSaved,
}: {
  target: HotelCompanyOption | "new";
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;
  const [name, setName] = useState(existing?.name ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("호텔사명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        await createHotelCompany(name.trim());
      } else if (existing) {
        await updateHotelCompany(existing.id, name.trim());
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "저장 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!existing || !confirm(`${existing.name} 호텔사를 사용 중지하시겠습니까?`)) return;
    setBusy(true);
    setError(null);
    try {
      await deactivateHotelCompany(existing.id);
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "사용 중지 중 오류가 발생했습니다."));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-background-50 p-6">
        <h3 className="font-heading text-lg font-semibold">
          {isNew ? "호텔사 추가" : `호텔사 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4">
          <FormField label="호텔사명">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
        </div>
        {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
        <div className="mt-6 flex items-center justify-between gap-2">
          {!isNew && (
            <button
              type="button"
              onClick={handleDeactivate}
              disabled={busy || submitting}
              className="rounded border border-accent-300 bg-accent-50 px-3 py-2 text-xs text-accent-700 hover:bg-accent-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
            >
              사용중지
            </button>
          )}
          <div className="ml-auto flex gap-2">
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
    </div>
  );
}

function HotelColumn({ company }: { company: HotelCompanyOption | null }) {
  const [hotels, setHotels] = useState<HotelOption[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [selectedHotelId, setSelectedHotelId] = useState<number | null>(null);
  const selectedHotel = hotels.find((h) => h.id === selectedHotelId) ?? null;
  const [form, setForm] = useState<HotelOption | "new" | null>(null);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setSelectedHotelId(null);
      setHotels([]);
      if (company) {
        setLoading(true);
        setError(null);
      }
    });

    if (!company) return;

    fetchHotels(company.id, false)
      .then((data) => {
        if (!cancelled) setHotels(data);
      })
      .catch(() => {
        if (!cancelled) setError("호텔 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [company, reloadToken]);

  return (
    <div className="p-4">
      <div className="mb-2 flex items-center justify-between">
        <h3 className="text-sm font-semibold text-foreground-800">호텔</h3>
        {company && (
          <button
            onClick={() => setForm("new")}
            className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
          >
            추가
          </button>
        )}
      </div>
      {!company && (
        <div className="py-6 text-center text-xs text-foreground-500">호텔사를 선택해 주세요.</div>
      )}
      {company && loading && (
        <div className="py-6 text-center text-xs text-foreground-500">불러오는 중...</div>
      )}
      {company && !loading && error && (
        <div className="py-6 text-center text-xs text-accent-700">{error}</div>
      )}
      {company && !loading && !error && hotels.length === 0 && (
        <div className="py-6 text-center text-xs text-foreground-500">등록된 호텔이 없습니다.</div>
      )}
      {company && !loading && !error && (
        <ul className="space-y-1">
          {hotels.map((h) => (
            <li
              key={h.id}
              onClick={() => setSelectedHotelId(h.id)}
              className={`flex items-center justify-between rounded px-2 py-1.5 text-sm cursor-pointer ${
                selectedHotel?.id === h.id
                  ? "bg-primary-50 text-primary-800"
                  : "hover:bg-background-100"
              }`}
            >
              <span className={h.active ? "" : "text-foreground-400 line-through"}>
                {h.name}
              </span>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setForm(h);
                }}
                className="text-[11px] text-foreground-500 hover:text-primary-600 cursor-pointer"
              >
                수정
              </button>
            </li>
          ))}
        </ul>
      )}

      {selectedHotel && (
        <div className="mt-4 border-t border-background-200/70 pt-4">
          <BranchColumn hotel={selectedHotel} />
        </div>
      )}

      {form && company && (
        <HotelFormModal
          target={form}
          hotelCompanyId={company.id}
          onClose={() => setForm(null)}
          onSaved={() => {
            setForm(null);
            setReloadToken((t) => t + 1);
          }}
        />
      )}
    </div>
  );
}

function HotelFormModal({
  target,
  hotelCompanyId,
  onClose,
  onSaved,
}: {
  target: HotelOption | "new";
  hotelCompanyId: number;
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;
  const [name, setName] = useState(existing?.name ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("호텔명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        await createHotel(hotelCompanyId, name.trim());
      } else if (existing) {
        await updateHotel(existing.id, hotelCompanyId, name.trim());
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "저장 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleActive = async () => {
    if (!existing) return;
    if (existing.active && !confirm(`${existing.name} 호텔을 사용 중지하시겠습니까?`)) return;
    setBusy(true);
    setError(null);
    try {
      if (existing.active) {
        await deactivateHotel(existing.id);
      } else {
        await activateHotel(existing.id);
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "처리 중 오류가 발생했습니다."));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-background-50 p-6">
        <h3 className="font-heading text-lg font-semibold">
          {isNew ? "호텔 추가" : `호텔 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4">
          <FormField label="호텔명">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
        </div>
        {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
        <div className="mt-6 flex items-center justify-between gap-2">
          {!isNew && (
            <button
              type="button"
              onClick={handleToggleActive}
              disabled={busy || submitting}
              className={`rounded border px-3 py-2 text-xs disabled:opacity-50 cursor-pointer whitespace-nowrap ${
                existing?.active
                  ? "border-accent-300 bg-accent-50 text-accent-700 hover:bg-accent-100"
                  : "border-secondary-300 bg-secondary-50 text-secondary-800 hover:bg-secondary-100"
              }`}
            >
              {existing?.active ? "사용중지" : "재활성화"}
            </button>
          )}
          <div className="ml-auto flex gap-2">
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
    </div>
  );
}

function BranchColumn({ hotel }: { hotel: HotelOption }) {
  const [branches, setBranches] = useState<BranchOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [form, setForm] = useState<BranchOption | "new" | null>(null);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setLoading(true);
      setError(null);
    });

    fetchBranches(hotel.id, false)
      .then((data) => {
        if (!cancelled) setBranches(data);
      })
      .catch(() => {
        if (!cancelled) setError("지점 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [hotel.id, reloadToken]);

  return (
    <div>
      <div className="mb-2 flex items-center justify-between">
        <h3 className="text-sm font-semibold text-foreground-800">{hotel.name} · 지점</h3>
        <button
          onClick={() => setForm("new")}
          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          추가
        </button>
      </div>
      {loading && <div className="py-4 text-center text-xs text-foreground-500">불러오는 중...</div>}
      {!loading && error && <div className="py-4 text-center text-xs text-accent-700">{error}</div>}
      {!loading && !error && branches.length === 0 && (
        <div className="py-4 text-center text-xs text-foreground-500">등록된 지점이 없습니다.</div>
      )}
      {!loading && !error && (
        <ul className="space-y-1">
          {branches.map((b) => (
            <li
              key={b.id}
              className="flex items-center justify-between rounded px-2 py-1.5 text-sm hover:bg-background-100"
            >
              <span className={b.active ? "" : "text-foreground-400 line-through"}>
                {b.name}
              </span>
              <button
                onClick={() => setForm(b)}
                className="text-[11px] text-foreground-500 hover:text-primary-600 cursor-pointer"
              >
                수정
              </button>
            </li>
          ))}
        </ul>
      )}

      {form && (
        <BranchFormModal
          target={form}
          hotelId={hotel.id}
          onClose={() => setForm(null)}
          onSaved={() => {
            setForm(null);
            setReloadToken((t) => t + 1);
          }}
        />
      )}
    </div>
  );
}

function BranchFormModal({
  target,
  hotelId,
  onClose,
  onSaved,
}: {
  target: BranchOption | "new";
  hotelId: number;
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;
  const [name, setName] = useState(existing?.name ?? "");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("지점명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        await createBranch(hotelId, name.trim());
      } else if (existing) {
        await updateBranch(existing.id, hotelId, name.trim());
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "저장 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleActive = async () => {
    if (!existing) return;
    if (existing.active && !confirm(`${existing.name} 지점을 사용 중지하시겠습니까?`)) return;
    setBusy(true);
    setError(null);
    try {
      if (existing.active) {
        await deactivateBranch(existing.id);
      } else {
        await activateBranch(existing.id);
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err, "처리 중 오류가 발생했습니다."));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-background-50 p-6">
        <h3 className="font-heading text-lg font-semibold">
          {isNew ? "지점 추가" : `지점 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4">
          <FormField label="지점명">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
        </div>
        {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
        <div className="mt-6 flex items-center justify-between gap-2">
          {!isNew && (
            <button
              type="button"
              onClick={handleToggleActive}
              disabled={busy || submitting}
              className={`rounded border px-3 py-2 text-xs disabled:opacity-50 cursor-pointer whitespace-nowrap ${
                existing?.active
                  ? "border-accent-300 bg-accent-50 text-accent-700 hover:bg-accent-100"
                  : "border-secondary-300 bg-secondary-50 text-secondary-800 hover:bg-secondary-100"
              }`}
            >
              {existing?.active ? "사용중지" : "재활성화"}
            </button>
          )}
          <div className="ml-auto flex gap-2">
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
    </div>
  );
}

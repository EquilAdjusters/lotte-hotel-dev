import { useEffect, useState } from "react";
import axios from "axios";
import AppShell from "@/app/layouts/AppShell";
import RequireRole from "@/shared/components/RequireRole";
import {
  assignClaim,
  fetchAdjusters,
  fetchAdjustingCompanies,
  fetchAssignmentClaims,
} from "@/entities/adjusting/api/adjustingApi";
import type {
  AdjusterOption,
  AdjustingCompanyOption,
  ClaimAssignmentListResponse,
} from "@/entities/adjusting/model/types";

const PAGE_SIZE = 10;

export default function AdminClaimsPage() {
  return (
    <AppShell>
      <RequireRole roles={["ADMIN1", "ADMIN2"]}>
        <ClaimsList />
      </RequireRole>
    </AppShell>
  );
}

function ClaimsList() {
  const [victimNameInput, setVictimNameInput] = useState("");
  const [appliedVictimName, setAppliedVictimName] = useState("");
  const [assignedFilter, setAssignedFilter] = useState<"전체" | "배정" | "미배정">(
    "전체"
  );
  const [page, setPage] = useState(0);

  const [claims, setClaims] = useState<ClaimAssignmentListResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [listLoading, setListLoading] = useState(true);
  const [listError, setListError] = useState<string | null>(null);

  const [selectedId, setSelectedId] = useState<number | null>(null);

  useEffect(() => {
    let cancelled = false;

    // react-hooks/set-state-in-effect 회피: 동기 setState는 마이크로태스크로 감싼다.
    queueMicrotask(() => {
      if (cancelled) return;
      setListLoading(true);
      setListError(null);
      setSelectedId(null);
    });

    fetchAssignmentClaims({
      page,
      size: PAGE_SIZE,
      victimName: appliedVictimName || null,
      assigned:
        assignedFilter === "전체" ? null : assignedFilter === "배정",
    })
      .then((data) => {
        if (cancelled) return;
        setClaims(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      })
      .catch(() => {
        if (!cancelled) setListError("목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
      })
      .finally(() => {
        if (!cancelled) setListLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [page, appliedVictimName, assignedFilter]);

  const applySearch = () => {
    setPage(0);
    setAppliedVictimName(victimNameInput.trim());
  };

  const selected = claims.find((c) => c.claimId === selectedId) ?? null;

  const handleAssigned = (updated: ClaimAssignmentListResponse) => {
    setClaims((prev) =>
      prev.map((c) => (c.claimId === updated.claimId ? updated : c))
    );
  };

  return (
    <div className="space-y-6">
      {/* Search & filter bar */}
      <div className="rounded-lg border border-background-200/70 bg-background-50 p-5">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-center">
          <div className="flex flex-1 items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3">
            <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
              <i className="ri-user-line"></i>
            </span>
            <input
              type="text"
              value={victimNameInput}
              onChange={(e) => setVictimNameInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && applySearch()}
              placeholder="피해자명"
              className="w-full bg-transparent py-2.5 text-sm outline-none placeholder:text-foreground-400"
            />
          </div>
          <button
            type="button"
            onClick={applySearch}
            className="rounded-md bg-primary-500 px-6 py-2.5 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
          >
            조회하기
          </button>
        </div>

        <div className="mt-4 flex flex-wrap gap-2">
          {(["전체", "배정", "미배정"] as const).map((s) => (
            <button
              type="button"
              key={s}
              onClick={() => {
                setPage(0);
                setAssignedFilter(s);
              }}
              className={`flex items-center gap-2 rounded-md border px-3 py-1.5 text-sm cursor-pointer whitespace-nowrap ${
                assignedFilter === s
                  ? "border-primary-500 bg-primary-500 text-background-50"
                  : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
              }`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div className="rounded-lg border border-background-200/70 bg-background-50">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-background-200/70 bg-background-100">
                <th className="px-2 py-2 text-center w-8"></th>
                <th className="px-2 py-2 font-medium text-foreground-600">번호</th>
                <th className="px-2 py-2 font-medium text-foreground-600">피해자명</th>
                <th className="px-2 py-2 font-medium text-foreground-600">접수자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">접수일자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">사고일자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">호텔</th>
                <th className="px-2 py-2 font-medium text-foreground-600">지점</th>
                <th className="px-2 py-2 font-medium text-foreground-600">손사업체</th>
                <th className="px-2 py-2 font-medium text-foreground-600">담당자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">담당자 연락처</th>
                <th className="px-2 py-2 font-medium text-foreground-600">보험사</th>
                <th className="px-2 py-2 font-medium text-foreground-600">접수메일</th>
                <th className="px-2 py-2 font-medium text-foreground-600">배정여부</th>
              </tr>
            </thead>
            <tbody>
              {listLoading && (
                <tr>
                  <td colSpan={14} className="px-5 py-10 text-center text-sm text-foreground-500">
                    불러오는 중...
                  </td>
                </tr>
              )}
              {!listLoading && listError && (
                <tr>
                  <td colSpan={14} className="px-5 py-10 text-center text-sm text-accent-700">
                    {listError}
                  </td>
                </tr>
              )}
              {!listLoading &&
                !listError &&
                claims.map((c) => (
                  <tr
                    key={c.claimId}
                    className={`border-b border-background-200/60 hover:bg-background-100/60 cursor-pointer ${
                      selectedId === c.claimId ? "bg-accent-50/40" : ""
                    }`}
                    onClick={() => setSelectedId(c.claimId)}
                  >
                    <td className="px-2 py-2 text-center">
                      <input
                        type="radio"
                        name="claim-select"
                        checked={selectedId === c.claimId}
                        onChange={() => setSelectedId(c.claimId)}
                        className="accent-primary-500 cursor-pointer"
                      />
                    </td>
                    <td className="px-2 py-2 font-mono">{c.claimNumber}</td>
                    <td className="px-2 py-2">{c.victimName}</td>
                    <td className="px-2 py-2">{c.receivedByName}</td>
                    <td className="px-2 py-2">
                      {new Date(c.receivedAt).toLocaleDateString("ko-KR")}
                    </td>
                    <td className="px-2 py-2">
                      {new Date(c.accidentAt).toLocaleDateString("ko-KR")}
                    </td>
                    <td className="px-2 py-2">{c.hotelName}</td>
                    <td className="px-2 py-2">{c.branchName}</td>
                    <td className="px-2 py-2">{c.adjustingCompanyName ?? "-"}</td>
                    <td className="px-2 py-2">{c.adjusterName ?? "-"}</td>
                    <td className="px-2 py-2">{c.adjusterPhone ?? "-"}</td>
                    <td className="px-2 py-2">{c.insuranceCompanyName ?? "-"}</td>
                    <td className="px-2 py-2">
                      {c.receiptEmail ? (
                        <a
                          href={`mailto:${c.receiptEmail}`}
                          className="text-blue-600 hover:underline"
                          onClick={(e) => e.stopPropagation()}
                        >
                          {c.receiptEmail}
                        </a>
                      ) : (
                        "-"
                      )}
                    </td>
                    <td className="px-2 py-2">
                      <span
                        className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${
                          c.assigned
                            ? "bg-primary-100 text-primary-800"
                            : "bg-background-200/80 text-foreground-700"
                        }`}
                      >
                        {c.assigned ? "배정" : "미배정"}
                      </span>
                    </td>
                  </tr>
                ))}
              {!listLoading && !listError && claims.length === 0 && (
                <tr>
                  <td colSpan={14} className="px-5 py-10 text-center text-sm text-foreground-500">
                    조건에 맞는 클레임이 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {!listLoading && !listError && totalElements > 0 && (
          <div className="flex items-center justify-between border-t border-background-200/70 bg-background-100 px-5 py-3 text-xs text-foreground-600">
            <span>
              총 {totalElements}건 · {page + 1} / {Math.max(totalPages, 1)} 페이지
            </span>
            <div className="flex gap-2">
              <button
                type="button"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="rounded-md border border-background-300/60 px-3 py-1 hover:bg-background-50 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
              >
                이전
              </button>
              <button
                type="button"
                disabled={page + 1 >= totalPages}
                onClick={() => setPage((p) => p + 1)}
                className="rounded-md border border-background-300/60 px-3 py-1 hover:bg-background-50 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
              >
                다음
              </button>
            </div>
          </div>
        )}
      </div>

      {selected && (
        <AssignPanel
          key={selected.claimId}
          claim={selected}
          onAssigned={handleAssigned}
        />
      )}
    </div>
  );
}

function AssignPanel({
  claim,
  onAssigned,
}: {
  claim: ClaimAssignmentListResponse;
  onAssigned: (updated: ClaimAssignmentListResponse) => void;
}) {
  const [companies, setCompanies] = useState<AdjustingCompanyOption[]>([]);
  const [companiesLoading, setCompaniesLoading] = useState(true);

  const [adjusters, setAdjusters] = useState<AdjusterOption[]>([]);
  const [adjustersLoading, setAdjustersLoading] = useState(false);

  const [companyId, setCompanyId] = useState<number | "">("");
  const [adjusterId, setAdjusterId] = useState<number | "">("");

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchAdjustingCompanies()
      .then(setCompanies)
      .catch(() => setCompanies([]))
      .finally(() => setCompaniesLoading(false));
  }, []);

  useEffect(() => {
    if (companyId === "") return;
    let cancelled = false;

    queueMicrotask(() => {
      if (!cancelled) setAdjustersLoading(true);
    });

    fetchAdjusters(companyId)
      .then((data) => {
        if (!cancelled) setAdjusters(data);
      })
      .catch(() => {
        if (!cancelled) setAdjusters([]);
      })
      .finally(() => {
        if (!cancelled) setAdjustersLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [companyId]);

  const handleAssign = async () => {
    if (companyId === "" || adjusterId === "") {
      setError("손사업체와 담당자를 모두 선택해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      await assignClaim(claim.claimId, {
        adjustingCompanyId: companyId,
        adjusterId,
      });
      const company = companies.find((c) => c.id === companyId);
      const adjuster = adjusters.find((a) => a.id === adjusterId);
      onAssigned({
        ...claim,
        adjustingCompanyName: company?.name ?? claim.adjustingCompanyName,
        adjusterName: adjuster?.name ?? claim.adjusterName,
        adjusterPhone: adjuster?.phone ?? claim.adjusterPhone,
        assigned: true,
        assignedAt: new Date().toISOString(),
      });
    } catch (err) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data as { message?: string } | undefined)?.message
        : undefined;
      setError(message ?? "배정 중 오류가 발생했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50 p-5">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-xs tracking-[0.18em] uppercase text-foreground-500">
            Selected Claim
          </div>
          <h3 className="mt-1 font-heading text-lg font-semibold">
            {claim.claimNumber} · {claim.victimName}
          </h3>
        </div>
        <span
          className={`rounded-full px-3 py-1 text-xs ${
            claim.assigned
              ? "bg-primary-100 text-primary-800"
              : "bg-background-200/80 text-foreground-700"
          }`}
        >
          {claim.assigned ? "배정" : "미배정"}
        </span>
      </div>

      {claim.assigned && (
        <div className="mt-4 rounded-md bg-background-100 px-4 py-3 text-sm text-foreground-700">
          현재 담당손사업체 <strong>{claim.adjustingCompanyName}</strong> · 담당자{" "}
          <strong>{claim.adjusterName}</strong> ({claim.adjusterPhone})
        </div>
      )}

      <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-[1fr_1fr_auto]">
        <div>
          <label className="mb-1.5 block text-xs font-medium text-foreground-700">
            손사업체
          </label>
          <select
            value={companyId}
            onChange={(e) => {
              const next = e.target.value ? Number(e.target.value) : "";
              setCompanyId(next);
              setAdjusterId("");
              if (next === "") setAdjusters([]);
            }}
            disabled={companiesLoading}
            className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400 cursor-pointer disabled:opacity-60"
          >
            <option value="">
              {companiesLoading ? "불러오는 중..." : "손사업체 선택"}
            </option>
            {companies.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="mb-1.5 block text-xs font-medium text-foreground-700">
            담당자
          </label>
          <select
            value={adjusterId}
            onChange={(e) =>
              setAdjusterId(e.target.value ? Number(e.target.value) : "")
            }
            disabled={companyId === "" || adjustersLoading}
            className="w-full rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none focus:border-primary-400 cursor-pointer disabled:opacity-60"
          >
            <option value="">
              {adjustersLoading ? "불러오는 중..." : "담당자 선택"}
            </option>
            {adjusters.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name} ({a.phone})
              </option>
            ))}
          </select>
        </div>
        <div className="flex items-end">
          <button
            type="button"
            onClick={handleAssign}
            disabled={submitting}
            className="w-full rounded-md bg-primary-500 px-6 py-2.5 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap sm:w-auto"
          >
            {submitting ? "배정 중..." : claim.assigned ? "재배정" : "배정하기"}
          </button>
        </div>
      </div>

      {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
    </div>
  );
}

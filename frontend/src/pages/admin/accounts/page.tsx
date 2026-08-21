import { Fragment, useEffect, useState } from "react";
import axios from "axios";
import AppShell from "@/app/layouts/AppShell";
import RequireRole from "@/shared/components/RequireRole";
import FormField from "@/shared/components/FormField";
import OrganizationSection from "@/pages/admin/accounts/OrganizationSection";
import BranchGroupSection from "@/pages/admin/accounts/BranchGroupSection";
import InsuranceCompanySection from "@/pages/admin/accounts/InsuranceCompanySection";
import type { Role, ScopeType } from "@/entities/user/model/types";
import {
  activateAccount,
  createAccount,
  deactivateAccount,
  deleteAccount,
  fetchAccountHistories,
  fetchAccounts,
  resetAccountPassword,
  unlockAccount,
  updateAccount,
} from "@/entities/account/api/accountApi";
import type {
  AccountCreatePayload,
  AccountHistoryResponse,
  AccountHistoryType,
  AccountResponse,
  AccountStatus,
  AccountUpdatePayload,
} from "@/entities/account/model/types";
import { fetchHotelCompanies } from "@/entities/hotel-company/api/hotelCompanyApi";
import type { HotelCompanyOption } from "@/entities/hotel-company/model/types";
import { fetchHotels } from "@/entities/hotel/api/hotelApi";
import type { HotelOption } from "@/entities/hotel/model/types";
import { fetchBranches } from "@/entities/branch/api/branchApi";
import type { BranchOption } from "@/entities/branch/model/types";
import { fetchBranchGroups } from "@/entities/branch-group/api/branchGroupApi";
import type { BranchGroupOption } from "@/entities/branch-group/model/types";
import {
  activateAdjustingCompany,
  activateAdjuster,
  createAdjustingCompany,
  createAdjuster,
  deactivateAdjustingCompany,
  deactivateAdjuster,
  fetchAdjusters,
  fetchAdjustingCompanies,
  updateAdjuster,
  updateAdjustingCompany,
} from "@/entities/adjusting/api/adjustingApi";
import type {
  AdjusterOption,
  AdjustingCompanyOption,
} from "@/entities/adjusting/model/types";

const roleLabels: Record<Role, string> = {
  ADMIN1: "와이즈 관리자",
  ADMIN2: "이퀼손사 센터장",
  ADMIN3: "호텔 본사 관리자",
  ADMIN4: "롯데 권역 영업지원팀",
  BRANCH_SHARED: "지점 공유계정",
};

const scopeLabels: Record<ScopeType, string> = {
  ALL: "전체 조회",
  ASSIGNED: "배정건만",
  HOTEL: "소속 호텔 전체",
  BRANCH: "소속 지점만",
  BRANCH_GROUP: "소속 권역",
};

const statusLabels: Record<AccountStatus, string> = {
  ACTIVE: "정상",
  INACTIVE: "사용중지",
  LOCKED: "잠김",
  DELETED: "말소",
};

const statusBadge: Record<AccountStatus, string> = {
  ACTIVE: "bg-primary-100 text-primary-800",
  INACTIVE: "bg-background-200/80 text-foreground-700",
  LOCKED: "bg-accent-100 text-accent-900",
  DELETED: "bg-background-100 text-foreground-500",
};

const historyTypeLabel: Record<AccountHistoryType, string> = {
  CREATED: "계정 생성",
  PASSWORD_CHANGED: "본인 비밀번호 변경",
  PASSWORD_RESET: "비밀번호 초기화",
  ROLE_CHANGED: "역할 변경",
  SCOPE_CHANGED: "조회범위 변경",
  STATUS_CHANGED: "상태 변경",
  AFFILIATION_CHANGED: "소속 변경",
  LOCKED: "잠금",
  UNLOCKED: "잠금해제",
  DELETED: "삭제",
  UPDATED: "정보 수정",
  ACTIVATED: "사용재개",
  DEACTIVATED: "사용중지",
};

function errorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const message = (err.response?.data as { message?: string } | undefined)?.message;
    if (message) return message;
  }
  return fallback;
}

export default function AdminAccountsPage() {
  return (
    <AppShell>
      <RequireRole roles={["ADMIN1"]}>
        <AdminAccountsContent />
      </RequireRole>
    </AppShell>
  );
}

function AdminAccountsContent() {
  return (
    <div className="space-y-8">
      <AccountSection />
      <AdjustingCompanySection />
      <OrganizationSection />
      <BranchGroupSection />
      <InsuranceCompanySection />
    </div>
  );
}

/* ═══════════════════════════ 계정현황 ═══════════════════════════ */

const PAGE_SIZE = 10;

function AccountSection() {
  const [keywordInput, setKeywordInput] = useState("");
  const [appliedKeyword, setAppliedKeyword] = useState("");
  const [roleFilter, setRoleFilter] = useState<"전체" | Role>("전체");
  const [statusFilter, setStatusFilter] = useState<"전체" | AccountStatus>("전체");
  const [page, setPage] = useState(0);

  const [accounts, setAccounts] = useState<AccountResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [listLoading, setListLoading] = useState(true);
  const [listError, setListError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  const [formTarget, setFormTarget] = useState<AccountResponse | "new" | null>(null);
  const [passwordTarget, setPasswordTarget] = useState<AccountResponse | null>(null);
  const [historyTarget, setHistoryTarget] = useState<AccountResponse | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [actionBusyId, setActionBusyId] = useState<number | null>(null);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setListLoading(true);
      setListError(null);
    });

    fetchAccounts({
      page,
      size: PAGE_SIZE,
      keyword: appliedKeyword || null,
      role: roleFilter === "전체" ? null : roleFilter,
      status: statusFilter === "전체" ? null : statusFilter,
    })
      .then((data) => {
        if (cancelled) return;
        setAccounts(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      })
      .catch(() => {
        if (!cancelled) setListError("계정 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setListLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [page, appliedKeyword, roleFilter, statusFilter, reloadToken]);

  const applySearch = () => {
    setPage(0);
    setAppliedKeyword(keywordInput.trim());
  };

  const reload = () => setReloadToken((t) => t + 1);

  const runAction = async (accountId: number, fn: () => Promise<unknown>) => {
    setActionBusyId(accountId);
    setActionError(null);
    try {
      await fn();
      reload();
    } catch (err) {
      setActionError(errorMessage(err, "처리 중 오류가 발생했습니다."));
    } finally {
      setActionBusyId(null);
    }
  };

  const orgLabel = (a: AccountResponse) =>
    [a.hotelCompanyName, a.hotelName, a.branchName, a.branchGroupName]
      .filter(Boolean)
      .join(" / ") || "-";

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">계정현황</h2>
        <button
          type="button"
          onClick={() => setFormTarget("new")}
          className="rounded-md bg-primary-500 px-4 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
        >
          <span className="mr-1">
            <i className="ri-add-line"></i>
          </span>
          계정 추가
        </button>
      </div>

      <div className="flex flex-col gap-3 border-b border-background-200/70 px-5 py-4 lg:flex-row lg:items-center">
        <div className="flex flex-1 items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3">
          <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
            <i className="ri-search-line"></i>
          </span>
          <input
            type="text"
            value={keywordInput}
            onChange={(e) => setKeywordInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && applySearch()}
            placeholder="아이디 또는 표시명 검색"
            className="w-full bg-transparent py-2.5 text-sm outline-none placeholder:text-foreground-400"
          />
        </div>
        <select
          value={roleFilter}
          onChange={(e) => {
            setPage(0);
            setRoleFilter(e.target.value as "전체" | Role);
          }}
          className="rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none cursor-pointer"
        >
          <option value="전체">역할 전체</option>
          {(Object.keys(roleLabels) as Role[]).map((r) => (
            <option key={r} value={r}>
              {roleLabels[r]}
            </option>
          ))}
        </select>
        <select
          value={statusFilter}
          onChange={(e) => {
            setPage(0);
            setStatusFilter(e.target.value as "전체" | AccountStatus);
          }}
          className="rounded-md border border-background-300/60 bg-background-50 px-3 py-2.5 text-sm outline-none cursor-pointer"
        >
          <option value="전체">상태 전체</option>
          {(Object.keys(statusLabels) as AccountStatus[]).map((s) => (
            <option key={s} value={s}>
              {statusLabels[s]}
            </option>
          ))}
        </select>
        <button
          type="button"
          onClick={applySearch}
          className="rounded-md bg-background-100 px-4 py-2.5 text-sm text-foreground-700 hover:bg-background-200 cursor-pointer whitespace-nowrap"
        >
          조회하기
        </button>
      </div>

      {actionError && (
        <div className="border-b border-background-200/70 bg-accent-50/60 px-5 py-2 text-xs text-accent-900">
          {actionError}
        </div>
      )}

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="border-b border-background-200/70 bg-background-100">
              <th className="px-3 py-2 font-medium text-foreground-600">역할</th>
              <th className="px-3 py-2 font-medium text-foreground-600">ID</th>
              <th className="px-3 py-2 font-medium text-foreground-600">표시명</th>
              <th className="px-3 py-2 font-medium text-foreground-600">소속</th>
              <th className="px-3 py-2 font-medium text-foreground-600">조회범위</th>
              <th className="px-3 py-2 font-medium text-foreground-600">상태</th>
              <th className="px-3 py-2 font-medium text-foreground-600">관리</th>
            </tr>
          </thead>
          <tbody>
            {listLoading && (
              <tr>
                <td colSpan={7} className="px-5 py-10 text-center text-sm text-foreground-500">
                  불러오는 중...
                </td>
              </tr>
            )}
            {!listLoading && listError && (
              <tr>
                <td colSpan={7} className="px-5 py-10 text-center text-sm text-accent-700">
                  {listError}
                </td>
              </tr>
            )}
            {!listLoading && !listError && accounts.length === 0 && (
              <tr>
                <td colSpan={7} className="px-5 py-10 text-center text-sm text-foreground-500">
                  등록된 계정이 없습니다.
                </td>
              </tr>
            )}
            {!listLoading &&
              !listError &&
              accounts.map((a) => {
                const busy = actionBusyId === a.id;
                return (
                  <tr key={a.id} className="border-b border-background-200/60 hover:bg-background-100/60">
                    <td className="px-3 py-2">{roleLabels[a.role]}</td>
                    <td className="px-3 py-2 font-mono">{a.loginId}</td>
                    <td className="px-3 py-2">{a.displayName}</td>
                    <td className="px-3 py-2">{orgLabel(a)}</td>
                    <td className="px-3 py-2">{scopeLabels[a.scopeType]}</td>
                    <td className="px-3 py-2">
                      <span
                        className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${statusBadge[a.status]}`}
                      >
                        {statusLabels[a.status]}
                      </span>
                    </td>
                    <td className="px-3 py-2">
                      <div className="flex flex-wrap gap-1">
                        <button
                          disabled={busy}
                          onClick={() => setFormTarget(a)}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-[11px] text-foreground-600 hover:bg-background-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                        >
                          수정
                        </button>
                        <button
                          disabled={busy}
                          onClick={() => setPasswordTarget(a)}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-[11px] text-foreground-600 hover:bg-background-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                        >
                          PW 초기화
                        </button>
                        <button
                          disabled={busy}
                          onClick={() => setHistoryTarget(a)}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-[11px] text-foreground-600 hover:bg-background-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                        >
                          이력
                        </button>
                        {a.status === "LOCKED" && (
                          <button
                            disabled={busy}
                            onClick={() => runAction(a.id, () => unlockAccount(a.id))}
                            className="rounded border border-secondary-300 bg-secondary-50 px-2 py-1 text-[11px] text-secondary-800 hover:bg-secondary-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                          >
                            잠금해제
                          </button>
                        )}
                        {a.status !== "DELETED" && (
                          <button
                            disabled={busy}
                            onClick={() =>
                              runAction(a.id, () =>
                                a.status === "ACTIVE"
                                  ? deactivateAccount(a.id)
                                  : activateAccount(a.id)
                              )
                            }
                            className="rounded border border-background-300 bg-background-50 px-2 py-1 text-[11px] text-foreground-600 hover:bg-background-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                          >
                            {a.status === "ACTIVE" ? "사용중지" : "사용재개"}
                          </button>
                        )}
                        {a.status !== "DELETED" && (
                          <button
                            disabled={busy}
                            onClick={() => {
                              if (!confirm(`${a.loginId} 계정을 삭제하시겠습니까?`)) return;
                              runAction(a.id, () => deleteAccount(a.id));
                            }}
                            className="rounded border border-accent-300 bg-accent-50 px-2 py-1 text-[11px] text-accent-700 hover:bg-accent-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                          >
                            삭제
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
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

      {formTarget && (
        <AccountFormModal
          target={formTarget}
          onClose={() => setFormTarget(null)}
          onSaved={() => {
            setFormTarget(null);
            reload();
          }}
        />
      )}

      {passwordTarget && (
        <PasswordResetModal
          account={passwordTarget}
          onClose={() => setPasswordTarget(null)}
          onDone={() => setPasswordTarget(null)}
        />
      )}

      {historyTarget && (
        <AccountHistoryModal
          account={historyTarget}
          onClose={() => setHistoryTarget(null)}
        />
      )}
    </div>
  );
}

function AccountFormModal({
  target,
  onClose,
  onSaved,
}: {
  target: AccountResponse | "new";
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;

  const [loginId, setLoginId] = useState(existing?.loginId ?? "");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState(existing?.displayName ?? "");
  const [role, setRole] = useState<Role>(existing?.role ?? "BRANCH_SHARED");
  const [scopeType, setScopeType] = useState<ScopeType>(existing?.scopeType ?? "BRANCH");
  const [sharedAccount, setSharedAccount] = useState(existing?.sharedAccount ?? true);

  const [hotelCompanyId, setHotelCompanyId] = useState<number | "">(
    existing?.hotelCompanyId ?? ""
  );
  const [hotelId, setHotelId] = useState<number | "">(existing?.hotelId ?? "");
  const [branchId, setBranchId] = useState<number | "">(existing?.branchId ?? "");
  const [branchGroupId, setBranchGroupId] = useState<number | "">(
    existing?.branchGroupId ?? ""
  );

  const [hotelCompanies, setHotelCompanies] = useState<HotelCompanyOption[]>([]);
  const [hotels, setHotels] = useState<HotelOption[]>([]);
  const [branches, setBranches] = useState<BranchOption[]>([]);
  const [branchGroups, setBranchGroups] = useState<BranchGroupOption[]>([]);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchHotelCompanies().then(setHotelCompanies).catch(() => setHotelCompanies([]));
    fetchBranchGroups().then(setBranchGroups).catch(() => setBranchGroups([]));
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

  const handleSubmit = async () => {
    if (isNew && (!loginId.trim() || !password.trim())) {
      setError("아이디와 비밀번호를 입력해 주세요.");
      return;
    }
    if (!displayName.trim()) {
      setError("표시명을 입력해 주세요.");
      return;
    }

    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        const payload: AccountCreatePayload = {
          loginId: loginId.trim(),
          password,
          displayName: displayName.trim(),
          role,
          scopeType,
          sharedAccount,
          hotelCompanyId: hotelCompanyId === "" ? null : hotelCompanyId,
          hotelId: hotelId === "" ? null : hotelId,
          branchId: branchId === "" ? null : branchId,
          branchGroupId: branchGroupId === "" ? null : branchGroupId,
        };
        await createAccount(payload);
      } else if (existing) {
        const payload: AccountUpdatePayload = {
          displayName: displayName.trim(),
          role,
          scopeType,
          sharedAccount,
          hotelCompanyId: hotelCompanyId === "" ? null : hotelCompanyId,
          hotelId: hotelId === "" ? null : hotelId,
          branchId: branchId === "" ? null : branchId,
          branchGroupId: branchGroupId === "" ? null : branchGroupId,
        };
        await updateAccount(existing.id, payload);
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
      <div className="w-full max-w-2xl rounded-lg bg-background-50 p-6 max-h-[90vh] overflow-y-auto">
        <h3 className="font-heading text-lg font-semibold">
          {isNew ? "계정 추가" : `계정 수정 · ${existing?.loginId}`}
        </h3>

        <div className="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2">
          {isNew && (
            <>
              <FormField label="아이디">
                <input
                  value={loginId}
                  onChange={(e) => setLoginId(e.target.value)}
                  className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
                />
              </FormField>
              <FormField label="초기 비밀번호">
                <input
                  type="text"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
                />
              </FormField>
            </>
          )}
          <FormField label="표시명">
            <input
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
          <FormField label="역할">
            <select
              value={role}
              onChange={(e) => setRole(e.target.value as Role)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
            >
              {(Object.keys(roleLabels) as Role[]).map((r) => (
                <option key={r} value={r}>
                  {roleLabels[r]}
                </option>
              ))}
            </select>
          </FormField>
          <FormField label="조회범위">
            <select
              value={scopeType}
              onChange={(e) => setScopeType(e.target.value as ScopeType)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
            >
              {(Object.keys(scopeLabels) as ScopeType[]).map((s) => (
                <option key={s} value={s}>
                  {scopeLabels[s]}
                </option>
              ))}
            </select>
          </FormField>
          <FormField label="공유계정 여부">
            <label className="flex h-[38px] items-center gap-2 text-sm text-foreground-700">
              <input
                type="checkbox"
                checked={sharedAccount}
                onChange={(e) => setSharedAccount(e.target.checked)}
                className="h-4 w-4 accent-primary-500 cursor-pointer"
              />
              여러 사람이 공동으로 사용하는 계정입니다
            </label>
          </FormField>

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
              <option value="">미지정</option>
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
              <option value="">미지정</option>
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
              <option value="">미지정</option>
              {branches.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.name}
                </option>
              ))}
            </select>
          </FormField>
          <FormField label="권역그룹">
            <select
              value={branchGroupId}
              onChange={(e) =>
                setBranchGroupId(e.target.value ? Number(e.target.value) : "")
              }
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
            >
              <option value="">미지정</option>
              {branchGroups.map((g) => (
                <option key={g.id} value={g.id}>
                  {g.name}
                </option>
              ))}
            </select>
          </FormField>
        </div>

        {error && <p className="mt-4 text-xs text-accent-700">{error}</p>}

        <div className="mt-6 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            disabled={submitting}
            className="rounded-md border border-background-300/60 px-5 py-2 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={submitting}
            className="rounded-md bg-primary-500 px-5 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
          >
            {submitting ? "저장 중..." : "저장하기"}
          </button>
        </div>
      </div>
    </div>
  );
}

function PasswordResetModal({
  account,
  onClose,
  onDone,
}: {
  account: AccountResponse;
  onClose: () => void;
  onDone: () => void;
}) {
  const [newPassword, setNewPassword] = useState("");
  const [newPasswordConfirm, setNewPasswordConfirm] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async () => {
    if (!newPassword || newPassword !== newPasswordConfirm) {
      setError("새 비밀번호와 확인값이 일치하지 않습니다.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      await resetAccountPassword(account.id, newPassword, newPasswordConfirm);
      onDone();
    } catch (err) {
      setError(errorMessage(err, "비밀번호 초기화 중 오류가 발생했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-background-50 p-6">
        <h3 className="font-heading text-lg font-semibold">
          비밀번호 초기화 · {account.loginId}
        </h3>
        <div className="mt-4 space-y-3">
          <FormField label="새 비밀번호">
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
          <FormField label="새 비밀번호 확인">
            <input
              type="password"
              value={newPasswordConfirm}
              onChange={(e) => setNewPasswordConfirm(e.target.value)}
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
            className="rounded-md border border-background-300/60 px-5 py-2 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={submitting}
            className="rounded-md bg-primary-500 px-5 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
          >
            {submitting ? "처리 중..." : "초기화"}
          </button>
        </div>
      </div>
    </div>
  );
}

function AccountHistoryModal({
  account,
  onClose,
}: {
  account: AccountResponse;
  onClose: () => void;
}) {
  const [histories, setHistories] = useState<AccountHistoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    fetchAccountHistories(account.id, { size: 50 })
      .then((data) => {
        if (!cancelled) setHistories(data.content);
      })
      .catch(() => {
        if (!cancelled) setError("이력을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [account.id]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-lg rounded-lg bg-background-50 p-6 max-h-[80vh] overflow-y-auto">
        <div className="flex items-center justify-between">
          <h3 className="font-heading text-lg font-semibold">
            계정 이력 · {account.loginId}
          </h3>
          <button
            type="button"
            onClick={onClose}
            className="w-8 h-8 flex items-center justify-center rounded-md text-foreground-500 hover:bg-background-100 cursor-pointer"
          >
            <i className="ri-close-line"></i>
          </button>
        </div>

        <div className="mt-4">
          {loading && (
            <div className="py-10 text-center text-sm text-foreground-500">불러오는 중...</div>
          )}
          {!loading && error && (
            <div className="py-10 text-center text-sm text-accent-700">{error}</div>
          )}
          {!loading && !error && histories.length === 0 && (
            <div className="py-10 text-center text-sm text-foreground-500">이력이 없습니다.</div>
          )}
          {!loading && !error && histories.length > 0 && (
            <ul className="space-y-3">
              {histories.map((h) => (
                <li
                  key={h.id}
                  className="rounded border border-background-200/70 bg-background-100/40 px-3 py-2.5"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-foreground-800">
                      {historyTypeLabel[h.actionType]}
                    </span>
                    <span className="text-[11px] text-foreground-500">
                      {new Date(h.createdAt).toLocaleString("ko-KR")}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-foreground-700">{h.description}</p>
                  <p className="mt-1 text-[11px] text-foreground-500">
                    {h.actorLoginId ?? "시스템"}
                    {h.actorIp ? ` · ${h.actorIp}` : ""}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}


/* ═══════════════════════════ 손사업체현황 ═══════════════════════════ */

function AdjustingCompanySection() {
  const [companies, setCompanies] = useState<AdjustingCompanyOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [hotelCompanies, setHotelCompanies] = useState<HotelCompanyOption[]>([]);

  const [formTarget, setFormTarget] = useState<AdjustingCompanyOption | "new" | null>(
    null
  );
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [actionBusyId, setActionBusyId] = useState<number | null>(null);

  const reload = () => setReloadToken((t) => t + 1);

  useEffect(() => {
    fetchHotelCompanies().then(setHotelCompanies).catch(() => setHotelCompanies([]));
  }, []);

  useEffect(() => {
    let cancelled = false;
    queueMicrotask(() => {
      if (!cancelled) {
        setLoading(true);
        setError(null);
      }
    });
    // activeOnly=false: 사용중지된 업체도 함께 보여줘야 재활성화할 수 있다.
    fetchAdjustingCompanies(false)
      .then((data) => {
        if (!cancelled) setCompanies(data);
      })
      .catch(() => {
        if (!cancelled) setError("손사업체 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [reloadToken]);

  const handleDeactivate = async (company: AdjustingCompanyOption) => {
    if (!confirm(`${company.name} 업체를 사용 중지하시겠습니까?`)) return;
    setActionBusyId(company.id);
    try {
      await deactivateAdjustingCompany(company.id);
      reload();
    } catch (err) {
      alert(errorMessage(err, "사용 중지 중 오류가 발생했습니다."));
    } finally {
      setActionBusyId(null);
    }
  };

  const handleActivate = async (company: AdjustingCompanyOption) => {
    setActionBusyId(company.id);
    try {
      await activateAdjustingCompany(company.id);
      reload();
    } catch (err) {
      alert(errorMessage(err, "재활성화 중 오류가 발생했습니다."));
    } finally {
      setActionBusyId(null);
    }
  };

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">손사업체현황</h2>
        <button
          type="button"
          onClick={() => setFormTarget("new")}
          className="rounded-md bg-primary-500 px-4 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
        >
          <span className="mr-1">
            <i className="ri-add-line"></i>
          </span>
          손사업체 추가
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-background-200/70 bg-background-100">
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">업체명</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">
                사업자번호
              </th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">
                적용 호텔사
              </th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">상태</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">관리</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={5} className="px-5 py-10 text-center text-sm text-foreground-500">
                  불러오는 중...
                </td>
              </tr>
            )}
            {!loading && error && (
              <tr>
                <td colSpan={5} className="px-5 py-10 text-center text-sm text-accent-700">
                  {error}
                </td>
              </tr>
            )}
            {!loading && !error && companies.length === 0 && (
              <tr>
                <td colSpan={5} className="px-5 py-10 text-center text-sm text-foreground-500">
                  등록된 손사업체가 없습니다.
                </td>
              </tr>
            )}
            {!loading &&
              !error &&
              companies.map((c) => (
                <Fragment key={c.id}>
                  <tr className="border-b border-background-200/60 hover:bg-background-100/60">
                    <td className="px-3 py-2">{c.name}</td>
                    <td className="px-3 py-2">{c.businessNumber || "-"}</td>
                    <td className="px-3 py-2 text-xs text-foreground-600">
                      {c.hotelCompanyIds.length === 0
                        ? "-"
                        : c.hotelCompanyIds
                            .map(
                              (id) =>
                                hotelCompanies.find((h) => h.id === id)?.name ?? "-"
                            )
                            .join(", ")}
                    </td>
                    <td className="px-3 py-2">
                      <span
                        className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${
                          c.active
                            ? "bg-primary-100 text-primary-800"
                            : "bg-background-200/80 text-foreground-700"
                        }`}
                      >
                        {c.active ? "활성" : "비활성"}
                      </span>
                    </td>
                    <td className="px-3 py-2">
                      <div className="flex flex-wrap gap-1">
                        <button
                          onClick={() =>
                            setExpandedId((prev) => (prev === c.id ? null : c.id))
                          }
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                        >
                          {expandedId === c.id ? "담당자 닫기" : "담당자 관리"}
                        </button>
                        <button
                          onClick={() => setFormTarget(c)}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                        >
                          수정
                        </button>
                        {c.active ? (
                          <button
                            disabled={actionBusyId === c.id}
                            onClick={() => handleDeactivate(c)}
                            className="rounded border border-accent-300 bg-accent-50 px-2 py-1 text-xs text-accent-700 hover:bg-accent-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                          >
                            사용중지
                          </button>
                        ) : (
                          <button
                            disabled={actionBusyId === c.id}
                            onClick={() => handleActivate(c)}
                            className="rounded border border-secondary-300 bg-secondary-50 px-2 py-1 text-xs text-secondary-800 hover:bg-secondary-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                          >
                            재활성화
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                  {expandedId === c.id && (
                    <tr>
                      <td colSpan={5} className="bg-background-100/50 px-5 py-4">
                        <AdjusterManager company={c} />
                      </td>
                    </tr>
                  )}
                </Fragment>
              ))}
          </tbody>
        </table>
      </div>

      {formTarget && (
        <AdjustingCompanyFormModal
          target={formTarget}
          hotelCompanies={hotelCompanies}
          onClose={() => setFormTarget(null)}
          onSaved={() => {
            setFormTarget(null);
            reload();
          }}
        />
      )}
    </div>
  );
}

function AdjustingCompanyFormModal({
  target,
  hotelCompanies,
  onClose,
  onSaved,
}: {
  target: AdjustingCompanyOption | "new";
  hotelCompanies: HotelCompanyOption[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const isNew = target === "new";
  const existing = isNew ? null : target;

  const [name, setName] = useState(existing?.name ?? "");
  const [businessNumber, setBusinessNumber] = useState(existing?.businessNumber ?? "");
  const [hotelCompanyIds, setHotelCompanyIds] = useState<number[]>(
    existing?.hotelCompanyIds ?? []
  );
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggleHotelCompany = (id: number) => {
    setHotelCompanyIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    );
  };

  const handleSubmit = async () => {
    if (!name.trim()) {
      setError("업체명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const payload = {
        name: name.trim(),
        businessNumber: businessNumber.trim() || null,
        hotelCompanyIds,
      };
      if (isNew) {
        await createAdjustingCompany(payload);
      } else if (existing) {
        await updateAdjustingCompany(existing.id, payload);
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
          {isNew ? "손사업체 추가" : `손사업체 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4 space-y-3">
          <FormField label="업체명">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
          <FormField label="사업자번호">
            <input
              value={businessNumber}
              onChange={(e) => setBusinessNumber(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </FormField>
          <FormField label="적용 호텔사">
            <div className="space-y-1.5 rounded-md border border-background-300/60 px-3 py-2">
              {hotelCompanies.length === 0 && (
                <p className="text-xs text-foreground-500">등록된 호텔사가 없습니다.</p>
              )}
              {hotelCompanies.map((h) => (
                <label key={h.id} className="flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    type="checkbox"
                    checked={hotelCompanyIds.includes(h.id)}
                    onChange={() => toggleHotelCompany(h.id)}
                  />
                  {h.name}
                </label>
              ))}
            </div>
            <p className="mt-1 text-[11px] text-foreground-500">
              체크한 호텔사의 접수건에서만 이 손사업체가 배정 대상으로 노출됩니다.
            </p>
          </FormField>
        </div>
        {error && <p className="mt-3 text-xs text-accent-700">{error}</p>}
        <div className="mt-6 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            disabled={submitting}
            className="rounded-md border border-background-300/60 px-5 py-2 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={submitting}
            className="rounded-md bg-primary-500 px-5 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
          >
            {submitting ? "저장 중..." : "저장하기"}
          </button>
        </div>
      </div>
    </div>
  );
}

function AdjusterManager({ company }: { company: AdjustingCompanyOption }) {
  const [adjusters, setAdjusters] = useState<AdjusterOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  const [showAdd, setShowAdd] = useState(false);
  const [newName, setNewName] = useState("");
  const [newPhone, setNewPhone] = useState("");
  const [editId, setEditId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");
  const [editPhone, setEditPhone] = useState("");
  const [busyId, setBusyId] = useState<number | "new" | null>(null);
  const [formError, setFormError] = useState<string | null>(null);

  const reload = () => setReloadToken((t) => t + 1);

  useEffect(() => {
    let cancelled = false;
    queueMicrotask(() => {
      if (!cancelled) {
        setLoading(true);
        setError(null);
      }
    });
    // activeOnly=false: 사용중지된 담당자도 함께 보여줘야 재활성화할 수 있다.
    fetchAdjusters(company.id, false)
      .then((data) => {
        if (!cancelled) setAdjusters(data);
      })
      .catch(() => {
        if (!cancelled) setError("담당자 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [company.id, reloadToken]);

  const handleAdd = async () => {
    if (!newName.trim() || !newPhone.trim()) {
      setFormError("담당자명과 연락처를 입력해 주세요.");
      return;
    }
    setBusyId("new");
    setFormError(null);
    try {
      await createAdjuster({
        adjustingCompanyId: company.id,
        name: newName.trim(),
        phone: newPhone.trim(),
      });
      setNewName("");
      setNewPhone("");
      setShowAdd(false);
      reload();
    } catch (err) {
      setFormError(errorMessage(err, "담당자 추가 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  const startEdit = (a: AdjusterOption) => {
    setEditId(a.id);
    setEditName(a.name);
    setEditPhone(a.phone);
  };

  const saveEdit = async () => {
    if (editId === null) return;
    if (!editName.trim() || !editPhone.trim()) {
      setFormError("담당자명과 연락처를 입력해 주세요.");
      return;
    }
    setBusyId(editId);
    setFormError(null);
    try {
      await updateAdjuster(editId, { name: editName.trim(), phone: editPhone.trim() });
      setEditId(null);
      reload();
    } catch (err) {
      setFormError(errorMessage(err, "담당자 수정 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  const handleDeactivate = async (a: AdjusterOption) => {
    if (!confirm(`${a.name} 담당자를 사용 중지하시겠습니까?`)) return;
    setBusyId(a.id);
    try {
      await deactivateAdjuster(a.id);
      reload();
    } catch (err) {
      setFormError(errorMessage(err, "사용 중지 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  const handleActivate = async (a: AdjusterOption) => {
    setBusyId(a.id);
    try {
      await activateAdjuster(a.id);
      reload();
    } catch (err) {
      setFormError(errorMessage(err, "재활성화 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div>
      <div className="mb-2 flex items-center justify-between">
        <div className="text-xs font-medium text-foreground-700">
          {company.name} 담당자 목록
        </div>
        <button
          onClick={() => setShowAdd((v) => !v)}
          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          담당자 추가
        </button>
      </div>

      {loading && <div className="py-4 text-center text-xs text-foreground-500">불러오는 중...</div>}
      {!loading && error && <div className="py-4 text-center text-xs text-accent-700">{error}</div>}
      {!loading && !error && adjusters.length === 0 && !showAdd && (
        <div className="py-4 text-center text-xs text-foreground-500">등록된 담당자가 없습니다.</div>
      )}

      {!loading && !error && adjusters.length > 0 && (
        <div className="space-y-1.5">
          {adjusters.map((a) => (
            <div
              key={a.id}
              className="flex items-center gap-2 rounded border border-background-200/70 bg-background-50 px-3 py-2 text-xs"
            >
              {editId === a.id ? (
                <>
                  <input
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    className="w-28 rounded border border-background-300 px-2 py-1 text-xs"
                  />
                  <input
                    value={editPhone}
                    onChange={(e) => setEditPhone(e.target.value)}
                    className="w-32 rounded border border-background-300 px-2 py-1 text-xs"
                  />
                  <button
                    onClick={saveEdit}
                    disabled={busyId === a.id}
                    className="rounded border border-primary-300 bg-primary-50 px-2 py-1 text-primary-700 hover:bg-primary-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                  >
                    저장
                  </button>
                  <button
                    onClick={() => setEditId(null)}
                    className="rounded border border-background-300 bg-background-50 px-2 py-1 text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                  >
                    취소
                  </button>
                </>
              ) : (
                <>
                  <span className="flex-1">{a.name}</span>
                  <span className="text-foreground-500">{a.phone}</span>
                  <span
                    className={`rounded-full px-2 py-0.5 text-[11px] ${
                      a.active
                        ? "bg-primary-100 text-primary-800"
                        : "bg-background-200/80 text-foreground-700"
                    }`}
                  >
                    {a.active ? "활성" : "비활성"}
                  </span>
                  <button
                    onClick={() => startEdit(a)}
                    className="rounded border border-background-300 bg-background-50 px-2 py-1 text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                  >
                    수정
                  </button>
                  {a.active ? (
                    <button
                      onClick={() => handleDeactivate(a)}
                      disabled={busyId === a.id}
                      className="rounded border border-accent-300 bg-accent-50 px-2 py-1 text-accent-700 hover:bg-accent-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                    >
                      사용중지
                    </button>
                  ) : (
                    <button
                      onClick={() => handleActivate(a)}
                      disabled={busyId === a.id}
                      className="rounded border border-secondary-300 bg-secondary-50 px-2 py-1 text-secondary-800 hover:bg-secondary-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                    >
                      재활성화
                    </button>
                  )}
                </>
              )}
            </div>
          ))}
        </div>
      )}

      {showAdd && (
        <div className="mt-2 flex flex-wrap items-center gap-2 rounded border border-background-200/70 bg-background-50 px-3 py-2">
          <input
            placeholder="담당자명"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            className="w-28 rounded border border-background-300 px-2 py-1 text-xs"
          />
          <input
            placeholder="연락처"
            value={newPhone}
            onChange={(e) => setNewPhone(e.target.value)}
            className="w-32 rounded border border-background-300 px-2 py-1 text-xs"
          />
          <button
            onClick={handleAdd}
            disabled={busyId === "new"}
            className="rounded border border-primary-300 bg-primary-50 px-2 py-1 text-xs text-primary-700 hover:bg-primary-100 disabled:opacity-50 cursor-pointer whitespace-nowrap"
          >
            저장
          </button>
        </div>
      )}

      {formError && <p className="mt-2 text-xs text-accent-700">{formError}</p>}
    </div>
  );
}

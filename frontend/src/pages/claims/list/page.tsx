import { useEffect, useState } from "react";
import axios from "axios";
import AppShell from "@/app/layouts/AppShell";
import { useAuth } from "@/shared/hooks/useAuth";
import {
  cancelClaim,
  deleteClaimAttachment,
  exportClaims,
  fetchClaim,
  fetchClaimAttachments,
  fetchClaimHistories,
  fetchClaims,
  updateClaim,
} from "@/entities/claim/api/claimApi";
import type {
  ClaimAttachmentResponse,
  ClaimHistoryResponse,
  ClaimListResponse,
  ClaimProgressStatus,
  ClaimResponse,
} from "@/entities/claim/model/types";
import {
  attachmentTypeToLabel,
  claimTypeLabelToEnum,
  claimTypeToLabel,
  consentMethodLabelToEnum,
  consentMethodToLabel,
  languageLabelToEnum,
  languageToLabel,
  nationalityLabelToVictimType,
  progressStatusBadge,
  progressStatusToLabel,
  victimTypeToLabel,
} from "@/entities/claim/lib/mappers";
import { sidoList, sigunguMap } from "@/shared/constants/regions";

const progressStatusList: ClaimProgressStatus[] = [
  "IN_PROGRESS",
  "CLOSED_PAID",
  "CLOSED_EXEMPTED",
  "CANCELLED",
];

const PAGE_SIZE = 10;

export default function ClaimListPage() {
  return (
    <AppShell>
      <ClaimListContent />
    </AppShell>
  );
}

function ClaimListContent() {
  const { user } = useAuth();
  const isAdmin1 = user?.role === "ADMIN1";
  const isBranchShared = user?.role === "BRANCH_SHARED";

  const [victimNameInput, setVictimNameInput] = useState("");
  const [receivedByNameInput, setReceivedByNameInput] = useState("");
  const [appliedVictimName, setAppliedVictimName] = useState("");
  const [appliedReceivedByName, setAppliedReceivedByName] = useState("");
  const [statusFilter, setStatusFilter] = useState<"전체" | ClaimProgressStatus>("전체");
  const [page, setPage] = useState(0);

  const [claims, setClaims] = useState<ClaimListResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [listLoading, setListLoading] = useState(true);
  const [listError, setListError] = useState<string | null>(null);

  const [selected, setSelected] = useState<ClaimListResponse | null>(null);
  const [detail, setDetail] = useState<ClaimResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    // 의존값이 바뀔 때마다 로딩/선택 상태를 초기화한다.
    // effect 본문에서 setState를 직접 호출하면 react-hooks/set-state-in-effect에 걸리므로
    // 마이크로태스크 콜백으로 감싼다.
    queueMicrotask(() => {
      if (cancelled) return;
      setListLoading(true);
      setListError(null);
      setSelected(null);
      setDetail(null);
      setDetailError(null);
    });

    fetchClaims({
      page,
      size: PAGE_SIZE,
      progressStatus: statusFilter === "전체" ? null : statusFilter,
      victimName: appliedVictimName || null,
      receivedByName: appliedReceivedByName || null,
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
  }, [page, statusFilter, appliedVictimName, appliedReceivedByName]);

  const applySearch = () => {
    setPage(0);
    setAppliedVictimName(victimNameInput.trim());
    setAppliedReceivedByName(receivedByNameInput.trim());
  };

  const selectRow = (row: ClaimListResponse) => {
    setSelected(row);
    setDetail(null);
    setDetailError(null);
    setDetailLoading(true);
    fetchClaim(row.claimId)
      .then(setDetail)
      .catch(() => setDetailError("상세 정보를 불러오지 못했습니다."))
      .finally(() => setDetailLoading(false));
  };

  const handleCancelled = (claimId: number) => {
    setClaims((prev) => prev.filter((c) => c.claimId !== claimId));
    setSelected(null);
    setDetail(null);
  };

  const handleUpdated = (updated: ClaimResponse) => {
    setDetail(updated);
    setClaims((prev) =>
      prev.map((c) =>
        c.claimId === updated.id
          ? { ...c, victimName: updated.victimName, receivedByName: updated.receivedByName }
          : c
      )
    );
    setSelected((prev) =>
      prev && prev.claimId === updated.id
        ? { ...prev, victimName: updated.victimName, receivedByName: updated.receivedByName }
        : prev
    );
  };

  const [exporting, setExporting] = useState(false);
  const [exportError, setExportError] = useState<string | null>(null);

  const handleExport = async () => {
    setExporting(true);
    setExportError(null);
    try {
      const blob = await exportClaims({
        progressStatus: statusFilter === "전체" ? null : statusFilter,
        victimName: appliedVictimName || null,
        receivedByName: appliedReceivedByName || null,
      });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `사고현황_${new Date().toISOString().slice(0, 10)}.xlsx`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (err) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data as { message?: string } | undefined)?.message
        : undefined;
      setExportError(message ?? "다운로드 중 오류가 발생했습니다.");
    } finally {
      setExporting(false);
    }
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
          <div className="flex flex-1 items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3">
            <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
              <i className="ri-user-voice-line"></i>
            </span>
            <input
              type="text"
              value={receivedByNameInput}
              onChange={(e) => setReceivedByNameInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && applySearch()}
              placeholder="접수자명"
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
          {!isBranchShared && (
            <button
              type="button"
              onClick={handleExport}
              disabled={exporting}
              className="flex items-center gap-1.5 rounded-md border border-background-300/60 bg-background-50 px-4 py-2.5 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
            >
              <i className="ri-download-2-line"></i>
              {exporting ? "다운로드 중..." : "엑셀 다운로드"}
            </button>
          )}
        </div>

        {exportError && (
          <p className="mt-2 text-xs text-accent-700">{exportError}</p>
        )}

        <div className="mt-4 flex flex-wrap gap-2">
          {(["전체", ...progressStatusList] as const).map((s) => (
            <button
              type="button"
              key={s}
              onClick={() => {
                setPage(0);
                setStatusFilter(s);
              }}
              className={`flex items-center gap-2 rounded-md border px-3 py-1.5 text-sm cursor-pointer whitespace-nowrap ${
                statusFilter === s
                  ? "border-primary-500 bg-primary-500 text-background-50"
                  : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
              }`}
            >
              {s === "전체" ? "전체" : progressStatusToLabel[s]}
            </button>
          ))}
        </div>
      </div>

      {/* List + Detail */}
      <div className="grid grid-cols-1 items-start gap-6 lg:grid-cols-[1.6fr_1fr]">
        {/* Left: claim list */}
        <div className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
          <div className="hidden md:grid grid-cols-[140px_1fr_150px_140px] gap-2 border-b border-background-200/70 bg-background-100 px-5 py-3 text-[11px] tracking-widest uppercase text-foreground-500">
            <span>번호</span>
            <span>피해자 / 접수자</span>
            <span>진행현황</span>
            <span>접수일시</span>
          </div>
          <ul className="divide-y divide-background-200/70">
            {listLoading && (
              <li className="px-5 py-16 text-center text-sm text-foreground-500">
                불러오는 중...
              </li>
            )}
            {!listLoading && listError && (
              <li className="px-5 py-16 text-center text-sm text-accent-700">{listError}</li>
            )}
            {!listLoading &&
              !listError &&
              claims.map((c) => {
                const isSelected = selected?.claimId === c.claimId;
                return (
                  <li
                    key={c.claimId}
                    onClick={() => selectRow(c)}
                    className={`grid cursor-pointer grid-cols-1 gap-2 px-5 py-4 transition md:grid-cols-[140px_1fr_150px_140px] md:items-center ${
                      isSelected ? "bg-primary-50" : "hover:bg-background-100"
                    }`}
                  >
                    <div className="font-mono text-xs text-foreground-600">{c.claimNumber}</div>
                    <div className="min-w-0">
                      <div className="truncate text-sm font-medium">
                        피해자 {c.victimName}
                      </div>
                      <div className="truncate text-xs text-foreground-500">
                        접수자 {c.receivedByName}
                      </div>
                    </div>
                    <div>
                      <span
                        className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${progressStatusBadge[c.progressStatus]}`}
                      >
                        {progressStatusToLabel[c.progressStatus]}
                      </span>
                    </div>
                    <div className="text-xs text-foreground-500">
                      {new Date(c.receivedAt).toLocaleString("ko-KR", {
                        month: "2-digit",
                        day: "2-digit",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </div>
                  </li>
                );
              })}
            {!listLoading && !listError && claims.length === 0 && (
              <li className="px-5 py-16 text-center text-sm text-foreground-500">
                조건에 맞는 클레임이 없습니다.
              </li>
            )}
          </ul>

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

        {/* Right: detail panel */}
        <aside className="rounded-lg border border-background-200/70 bg-background-50">
          {selected ? (
            <DetailPanel
              row={selected}
              detail={detail}
              loading={detailLoading}
              error={detailError}
              isAdmin1={isAdmin1}
              isBranchShared={isBranchShared}
              onClose={() => {
                setSelected(null);
                setDetail(null);
              }}
              onCancelled={() => handleCancelled(selected.claimId)}
              onUpdated={handleUpdated}
            />
          ) : (
            <div className="flex h-full min-h-[400px] flex-col items-center justify-center gap-3 p-10 text-center">
              <span className="w-12 h-12 flex items-center justify-center rounded-full bg-background-100 text-foreground-500">
                <i className="ri-file-search-line text-xl"></i>
              </span>
              <div className="text-sm text-foreground-600">
                왼쪽 목록에서 항목을 선택하면
                <br />
                상세 내용 확인이 가능합니다.
              </div>
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}

function DetailPanel({
  row,
  detail,
  loading,
  error,
  isAdmin1,
  isBranchShared,
  onClose,
  onCancelled,
  onUpdated,
}: {
  row: ClaimListResponse;
  detail: ClaimResponse | null;
  loading: boolean;
  error: string | null;
  isAdmin1: boolean;
  isBranchShared: boolean;
  onClose: () => void;
  onCancelled: () => void;
  onUpdated: (updated: ClaimResponse) => void;
}) {
  const [showContactModal, setShowContactModal] = useState(false);
  const [showCancelConfirm, setShowCancelConfirm] = useState(false);
  const [cancelling, setCancelling] = useState(false);
  const [cancelError, setCancelError] = useState<string | null>(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  const maskPhone = (phone?: string | null) => {
    if (!phone) return "-";
    const parts = phone.split("-");
    if (parts.length === 3) return `${parts[0]}-****-${parts[2]}`;
    return phone;
  };

  const formatDate = (d?: string | null) => {
    if (!d) return "-";
    return new Date(d).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
  };

  const formatDateTime = (d?: string | null) => {
    if (!d) return "-";
    return new Date(d).toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const canCancel = row.progressStatus === "IN_PROGRESS";

  const handleCancelClick = () => {
    if (isAdmin1) {
      setCancelError(null);
      setShowCancelConfirm(true);
    } else {
      setShowContactModal(true);
    }
  };

  const confirmCancel = async () => {
    setCancelling(true);
    setCancelError(null);
    try {
      await cancelClaim(row.claimId);
      setShowCancelConfirm(false);
      onCancelled();
    } catch (err) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data as { message?: string } | undefined)?.message
        : undefined;
      setCancelError(message ?? "접수 취소 중 오류가 발생했습니다.");
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <div className="flex items-start justify-between border-b border-background-200/70 px-5 py-4">
        <div>
          <div className="font-mono text-xs text-foreground-500">{row.claimNumber}</div>
          <h3 className="mt-1 font-heading text-lg font-semibold">
            {detail ? claimTypeToLabel[detail.claimType] : " "}
          </h3>
          <div className="mt-1 text-sm text-foreground-600">
            {detail ? `${detail.hotelName} / ${detail.branchName}` : ""}
          </div>
        </div>
        <button
          type="button"
          onClick={onClose}
          className="w-8 h-8 flex items-center justify-center rounded-md text-foreground-500 hover:bg-background-100 cursor-pointer"
        >
          <i className="ri-close-line"></i>
        </button>
      </div>

      <div className="flex-1 px-5 py-5 space-y-5 overflow-auto">
        {/* Status */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-sm font-medium text-foreground-700">상태</span>
            <span className="text-foreground-400">:</span>
            <span
              className={`inline-flex items-center rounded-full px-3 py-0.5 text-xs ${progressStatusBadge[row.progressStatus]}`}
            >
              {progressStatusToLabel[row.progressStatus]}
            </span>
          </div>
          <button
            type="button"
            onClick={() => setShowHistoryModal(true)}
            className="flex items-center gap-1 text-xs text-foreground-600 hover:text-primary-600 cursor-pointer whitespace-nowrap"
          >
            <i className="ri-history-line"></i>
            이력 보기
          </button>
        </div>

        {loading && (
          <div className="py-10 text-center text-sm text-foreground-500">
            상세 정보를 불러오는 중...
          </div>
        )}
        {!loading && error && (
          <div className="py-10 text-center text-sm text-accent-700">{error}</div>
        )}

        {!loading && !error && detail && (
          <>
            {/* Main info table */}
            <div className="border border-background-200/70 rounded overflow-hidden text-sm">
              <DetailRow label="피해자명" value={detail.victimName} />
              <DetailRow label="생년월일" value={detail.victimBirthDate} />
              <DetailRow label="휴대전화번호" value={maskPhone(detail.victimPhone)} />
              <DetailRow
                label="내·외국인"
                value={
                  detail.preferredLanguage
                    ? `${victimTypeToLabel[detail.victimType]} (${detail.preferredLanguage})`
                    : victimTypeToLabel[detail.victimType]
                }
              />
              <DetailRow
                label="거주지역"
                value={[detail.residenceSido, detail.residenceSigungu, detail.residenceDetail]
                  .filter(Boolean)
                  .join(" ")}
              />
              <DetailRow label="피해장소" value={`${detail.hotelName} / ${detail.branchName}`} />
              <DetailRow label="사고일시" value={formatDateTime(detail.accidentAt)} />
              <DetailRow label="사고유형" value={claimTypeToLabel[detail.claimType]} />
              <DetailRow label="사고경위" value={detail.accidentDescription} />
              <DetailRow
                label="동의 취득"
                value={
                  detail.consentMethod
                    ? `${formatDate(detail.consentObtainedAt)} / ${consentMethodToLabel[detail.consentMethod]}`
                    : "-"
                }
              />
              <DetailRow
                label="접수자"
                value={`${detail.receivedByName} (내선 ${detail.receivedByExtension})`}
              />
              <DetailRow label="접수일자" value={formatDate(detail.createdAt)} isLast />
            </div>

            {/* 첨부파일: BRANCH_SHARED 전용 (백엔드 @PreAuthorize와 동일) */}
            {isBranchShared && <AttachmentPanel claimId={row.claimId} />}

            {/* Assignment info table */}
            <div className="border border-background-200/70 rounded overflow-hidden text-sm">
              <DetailRow label="담당손사업체" value={detail.adjustingCompanyName ?? "-"} />
              <DetailRow label="담당자명" value={detail.adjusterName ?? "-"} />
              <DetailRow label="담당자 유선연락처" value={detail.adjusterPhone ?? "-"} isLast />
            </div>

            {/* Note */}
            <div className="rounded bg-background-100 p-3 text-xs text-foreground-600 leading-relaxed">
              [법무근거] 상세 화면의 생년월일·연락처는 마스킹 처리하고, 조회·다운로드 이력을 기록합니다.
            </div>

            {/* Edit / Cancel buttons */}
            {canCancel && (
              <div className="pt-2 flex justify-center gap-2">
                {isBranchShared && canCancel && (
                  <button
                    type="button"
                    onClick={() => setShowEditModal(true)}
                    className="rounded border border-primary-300 bg-primary-50 px-8 py-2 text-sm text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
                  >
                    수정하기
                  </button>
                )}
                {canCancel && (
                  <button
                    type="button"
                    onClick={handleCancelClick}
                    className="rounded bg-background-200 px-8 py-2 text-sm text-foreground-700 hover:bg-background-300/60 cursor-pointer whitespace-nowrap"
                  >
                    접수 취소하기
                  </button>
                )}
              </div>
            )}
          </>
        )}
      </div>

      {/* Non-ADMIN1: contact-WISE popup */}
      {showContactModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-sm rounded bg-background-50 p-8 text-center">
            <p className="text-sm text-foreground-800 leading-relaxed">
              접수취소를 위해선 와이즈 보험중개로 문의 주세요.
            </p>
            <p className="mt-1 text-base font-semibold text-foreground-900">02-000-0000</p>
            <button
              type="button"
              onClick={() => setShowContactModal(false)}
              className="mt-6 w-full rounded bg-primary-500 py-2 text-sm text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
            >
              확인
            </button>
          </div>
        </div>
      )}

      {/* ADMIN1: real cancel confirm */}
      {showCancelConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-sm rounded bg-background-50 p-8 text-center">
            <p className="text-sm text-foreground-800 leading-relaxed">
              접수를 취소하시겠습니까?
            </p>
            {cancelError && (
              <p className="mt-3 text-xs text-accent-700">{cancelError}</p>
            )}
            <div className="mt-6 flex gap-2">
              <button
                type="button"
                onClick={() => setShowCancelConfirm(false)}
                disabled={cancelling}
                className="flex-1 rounded border border-background-300/60 py-2 text-sm text-foreground-700 hover:bg-background-100 disabled:opacity-60 cursor-pointer whitespace-nowrap"
              >
                닫기
              </button>
              <button
                type="button"
                onClick={confirmCancel}
                disabled={cancelling}
                className="flex-1 rounded bg-accent-600 py-2 text-sm text-background-50 hover:bg-accent-700 disabled:opacity-70 cursor-pointer whitespace-nowrap"
              >
                {cancelling ? "취소 처리 중..." : "취소 확정"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* BRANCH_SHARED: edit claim */}
      {showEditModal && detail && (
        <EditClaimModal
          claim={detail}
          onClose={() => setShowEditModal(false)}
          onSaved={(updated) => {
            setShowEditModal(false);
            onUpdated(updated);
          }}
        />
      )}

      {/* 접수 이력 */}
      {showHistoryModal && (
        <HistoryModal claimId={row.claimId} onClose={() => setShowHistoryModal(false)} />
      )}
    </div>
  );
}

const historyTypeLabel: Record<ClaimHistoryResponse["historyType"], string> = {
  CREATED: "접수 생성",
  STATUS_CHANGED: "상태 변경",
  CLOSED: "종결",
  CONSENT_UPDATED: "동의정보 수정",
  ASSIGNED: "손사 배정",
  REASSIGNED: "손사 재배정",
  UPDATED: "접수 정보 수정",
  CANCELLED: "접수 취소",
  ASSIGNMENT_CHANGED: "배정 변경",
};

const sourceTypeLabel: Record<ClaimHistoryResponse["sourceType"], string> = {
  USER: "사용자",
  SYSTEM: "시스템",
  EXTERNAL_ADAPTER: "외부연동",
};

function HistoryModal({
  claimId,
  onClose,
}: {
  claimId: number;
  onClose: () => void;
}) {
  const [histories, setHistories] = useState<ClaimHistoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    fetchClaimHistories(claimId, { size: 50 })
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
  }, [claimId]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-lg rounded-lg bg-background-50 p-6 max-h-[80vh] overflow-y-auto">
        <div className="flex items-center justify-between">
          <h3 className="font-heading text-lg font-semibold">접수 이력</h3>
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
            <div className="py-10 text-center text-sm text-foreground-500">
              불러오는 중...
            </div>
          )}
          {!loading && error && (
            <div className="py-10 text-center text-sm text-accent-700">{error}</div>
          )}
          {!loading && !error && histories.length === 0 && (
            <div className="py-10 text-center text-sm text-foreground-500">
              이력이 없습니다.
            </div>
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
                      {historyTypeLabel[h.historyType]}
                    </span>
                    <span className="text-[11px] text-foreground-500">
                      {new Date(h.createdAt).toLocaleString("ko-KR")}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-foreground-700">{h.description}</p>
                  <p className="mt-1 text-[11px] text-foreground-500">
                    {h.actorLoginId ?? sourceTypeLabel[h.sourceType]}
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

function AttachmentPanel({ claimId }: { claimId: number }) {
  const [attachments, setAttachments] = useState<ClaimAttachmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [busyId, setBusyId] = useState<number | null>(null);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setLoading(true);
      setError(null);
    });

    fetchClaimAttachments(claimId)
      .then((data) => {
        if (!cancelled) setAttachments(data);
      })
      .catch(() => {
        if (!cancelled) setError("첨부파일 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [claimId, reloadToken]);

  const formatSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes}B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)}KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)}MB`;
  };

  const handleDelete = async (attachment: ClaimAttachmentResponse) => {
    if (!confirm(`${attachment.originalFileName} 파일을 삭제하시겠습니까?`)) return;
    setBusyId(attachment.id);
    try {
      await deleteClaimAttachment(claimId, attachment.id);
      setReloadToken((t) => t + 1);
    } catch (err) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data as { message?: string } | undefined)?.message
        : undefined;
      alert(message ?? "삭제 중 오류가 발생했습니다.");
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="border border-background-200/70 rounded overflow-hidden text-sm">
      <div className="bg-background-100 px-3 py-2 text-xs font-medium text-foreground-600">
        첨부파일
      </div>
      <div className="p-3">
        {loading && (
          <div className="py-3 text-center text-xs text-foreground-500">불러오는 중...</div>
        )}
        {!loading && error && (
          <div className="py-3 text-center text-xs text-accent-700">{error}</div>
        )}
        {!loading && !error && attachments.length === 0 && (
          <div className="py-3 text-center text-xs text-foreground-500">
            첨부된 파일이 없습니다.
          </div>
        )}
        {!loading && !error && attachments.length > 0 && (
          <ul className="space-y-1.5">
            {attachments.map((a) => (
              <li
                key={a.id}
                className="flex items-center justify-between gap-2 rounded border border-background-200/70 px-2.5 py-1.5 text-xs"
              >
                <div className="min-w-0">
                  <div className="truncate font-medium text-foreground-800">
                    {a.originalFileName}
                  </div>
                  <div className="text-foreground-500">
                    {attachmentTypeToLabel[a.attachmentType]} · {formatSize(a.fileSize)} ·{" "}
                    {new Date(a.createdAt).toLocaleDateString("ko-KR")}
                  </div>
                </div>
                <button
                  type="button"
                  onClick={() => handleDelete(a)}
                  disabled={busyId === a.id}
                  className="shrink-0 text-accent-700 hover:text-accent-800 disabled:opacity-50 cursor-pointer whitespace-nowrap"
                >
                  삭제
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

function EditClaimModal({
  claim,
  onClose,
  onSaved,
}: {
  claim: ClaimResponse;
  onClose: () => void;
  onSaved: (updated: ClaimResponse) => void;
}) {
  const [victimName, setVictimName] = useState(claim.victimName);
  const [victimPhone, setVictimPhone] = useState(claim.victimPhone);
  const [victimBirthDate, setVictimBirthDate] = useState(claim.victimBirthDate);
  const [victimNationality, setVictimNationality] = useState(
    claim.victimType === "FOREIGNER" ? "외국인" : "내국인"
  );
  const [victimLanguage, setVictimLanguage] = useState(
    claim.preferredLanguage ? languageToLabel[claim.preferredLanguage] : "한국어"
  );
  const [residenceSido, setResidenceSido] = useState(claim.residenceSido);
  const [residenceSigungu, setResidenceSigungu] = useState(claim.residenceSigungu);
  const [residenceDetail, setResidenceDetail] = useState(claim.residenceDetail ?? "");
  const [claimTypeLabel, setClaimTypeLabel] = useState(claimTypeToLabel[claim.claimType]);
  const [accidentDate, setAccidentDate] = useState(claim.accidentAt.slice(0, 10));
  const [accidentTime, setAccidentTime] = useState(claim.accidentAt.slice(11, 16));
  const [accidentDescription, setAccidentDescription] = useState(
    claim.accidentDescription
  );
  const [receivedByName, setReceivedByName] = useState(claim.receivedByName);
  const [receivedByExtension, setReceivedByExtension] = useState(
    claim.receivedByExtension
  );
  const [consentMethodLabel, setConsentMethodLabel] = useState(
    claim.consentMethod ? consentMethodToLabel[claim.consentMethod] : "서면"
  );
  const [consentDate, setConsentDate] = useState(
    claim.consentObtainedAt ? claim.consentObtainedAt.slice(0, 10) : ""
  );

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isForeigner = victimNationality === "외국인";

  const handleSubmit = async () => {
    if (
      !victimName.trim() ||
      !victimPhone.trim() ||
      !victimBirthDate ||
      !residenceSido ||
      !residenceSigungu ||
      !accidentDate ||
      !accidentTime ||
      !accidentDescription.trim() ||
      !receivedByName.trim() ||
      !receivedByExtension.trim() ||
      !consentDate
    ) {
      setError("필수 항목을 모두 입력해 주세요.");
      return;
    }
    if (accidentDescription.length > 200) {
      setError("사고경위는 200자 이내로 입력해 주세요.");
      return;
    }

    const consentTime = claim.consentObtainedAt
      ? claim.consentObtainedAt.slice(11, 19)
      : new Date().toISOString().slice(11, 19);

    setSubmitting(true);
    setError(null);
    try {
      const updated = await updateClaim(claim.id, {
        victimName: victimName.trim(),
        victimPhone: victimPhone.trim(),
        victimBirthDate,
        victimType: nationalityLabelToVictimType[victimNationality],
        preferredLanguage: isForeigner ? languageLabelToEnum[victimLanguage] : null,
        residenceSido,
        residenceSigungu,
        residenceDetail: residenceDetail.trim() || null,
        claimType: claimTypeLabelToEnum[claimTypeLabel],
        accidentAt: `${accidentDate}T${accidentTime}`,
        accidentDescription: accidentDescription.trim(),
        receivedByName: receivedByName.trim(),
        receivedByExtension: receivedByExtension.trim(),
        consent: {
          consentStatus: "OBTAINED",
          consentObtainedAt: `${consentDate}T${consentTime}`,
          consentMethod: consentMethodLabelToEnum[consentMethodLabel],
        },
      });
      onSaved(updated);
    } catch (err) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data as { message?: string } | undefined)?.message
        : undefined;
      setError(message ?? "수정 중 오류가 발생했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-2xl rounded-lg bg-background-50 p-6 max-h-[90vh] overflow-y-auto">
        <h3 className="font-heading text-lg font-semibold">
          접수 수정 · {claim.claimNumber}
        </h3>

        <div className="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <EditField label="피해자명">
            <input
              value={victimName}
              onChange={(e) => setVictimName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="휴대전화번호">
            <input
              value={victimPhone}
              onChange={(e) => setVictimPhone(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="생년월일">
            <input
              type="date"
              value={victimBirthDate}
              onChange={(e) => setVictimBirthDate(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="내·외국인">
            <div className="flex h-[38px] items-center gap-4">
              {["내국인", "외국인"].map((v) => (
                <label key={v} className="flex items-center gap-1.5 text-sm text-foreground-700 cursor-pointer">
                  <input
                    type="radio"
                    name="edit-nationality"
                    checked={victimNationality === v}
                    onChange={() => setVictimNationality(v)}
                    className="h-4 w-4 accent-primary-500 cursor-pointer"
                  />
                  {v}
                </label>
              ))}
            </div>
          </EditField>
          {isForeigner && (
            <EditField label="사용언어">
              <select
                value={victimLanguage}
                onChange={(e) => setVictimLanguage(e.target.value)}
                className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
              >
                {["영어", "중국어", "일어", "한국어"].map((l) => (
                  <option key={l} value={l}>
                    {l}
                  </option>
                ))}
              </select>
            </EditField>
          )}
          <EditField label="거주 시·도">
            <select
              value={residenceSido}
              onChange={(e) => {
                setResidenceSido(e.target.value);
                setResidenceSigungu("");
              }}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
            >
              <option value="">선택</option>
              {sidoList.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </EditField>
          <EditField label="거주 시·군·구">
            <select
              value={residenceSigungu}
              onChange={(e) => setResidenceSigungu(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
            >
              <option value="">선택</option>
              {(sigunguMap[residenceSido] ?? []).map((g) => (
                <option key={g} value={g}>
                  {g}
                </option>
              ))}
            </select>
          </EditField>
          <EditField label="상세주소">
            <input
              value={residenceDetail}
              onChange={(e) => setResidenceDetail(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="사고유형">
            <div className="flex h-[38px] items-center gap-4">
              {["재물사고", "배상사고"].map((t) => (
                <label key={t} className="flex items-center gap-1.5 text-sm text-foreground-700 cursor-pointer">
                  <input
                    type="radio"
                    name="edit-claim-type"
                    checked={claimTypeLabel === t}
                    onChange={() => setClaimTypeLabel(t)}
                    className="h-4 w-4 accent-primary-500 cursor-pointer"
                  />
                  {t}
                </label>
              ))}
            </div>
          </EditField>
          <EditField label="사고일시">
            <div className="flex gap-2">
              <input
                type="date"
                value={accidentDate}
                onChange={(e) => setAccidentDate(e.target.value)}
                className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
              />
              <input
                type="time"
                value={accidentTime}
                onChange={(e) => setAccidentTime(e.target.value)}
                className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
              />
            </div>
          </EditField>
          <div className="sm:col-span-2">
            <EditField label={`사고경위 (${accidentDescription.length}/200자)`}>
              <textarea
                value={accidentDescription}
                onChange={(e) => setAccidentDescription(e.target.value)}
                rows={3}
                maxLength={200}
                className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
              />
            </EditField>
          </div>
          <EditField label="접수자명">
            <input
              value={receivedByName}
              onChange={(e) => setReceivedByName(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="접수자 내선번호">
            <input
              value={receivedByExtension}
              onChange={(e) => setReceivedByExtension(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
          <EditField label="동의 수단">
            <div className="flex h-[38px] items-center gap-4">
              {["서면", "문자", "구두"].map((m) => (
                <label key={m} className="flex items-center gap-1.5 text-sm text-foreground-700 cursor-pointer">
                  <input
                    type="radio"
                    name="edit-consent-method"
                    checked={consentMethodLabel === m}
                    onChange={() => setConsentMethodLabel(m)}
                    className="h-4 w-4 accent-primary-500 cursor-pointer"
                  />
                  {m}
                </label>
              ))}
            </div>
          </EditField>
          <EditField label="동의 취득일">
            <input
              type="date"
              value={consentDate}
              onChange={(e) => setConsentDate(e.target.value)}
              className="w-full rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none focus:border-primary-400"
            />
          </EditField>
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

function EditField({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="mb-1.5 block text-xs font-medium text-foreground-700">
        {label}
      </label>
      {children}
    </div>
  );
}

function DetailRow({
  label,
  value,
  isLast,
}: {
  label: string;
  value: string;
  isLast?: boolean;
}) {
  return (
    <div
      className={`grid grid-cols-[120px_1fr] ${isLast ? "" : "border-b border-background-200/70"}`}
    >
      <div className="bg-background-100 px-3 py-2 text-xs text-foreground-600 flex items-center">
        {label}
      </div>
      <div className="px-3 py-2 text-sm text-foreground-800 flex items-center">{value}</div>
    </div>
  );
}

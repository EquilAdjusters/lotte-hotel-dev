import { useState } from "react";
import AppShell from "@/app/layouts/AppShell";
import { statusBadge } from "@/shared/constants/sample-incidents";
import {
  sampleClaimList,
  insurerAssignOptions,
  type ClaimListItem,
} from "@/shared/constants/sample-admin";

export default function AdminClaimsPage() {
  return (
    <AppShell>
      <ClaimsList />
    </AppShell>
  );
}

function ClaimsList() {
  const [claims, setClaims] = useState<ClaimListItem[]>(sampleClaimList);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [editId, setEditId] = useState<string | null>(null);
  const [draft, setDraft] = useState<Partial<ClaimListItem>>({});

  const selected = claims.find((c) => c.id === selectedId);

  const updateClaim = (id: string, data: Partial<ClaimListItem>) => {
    setClaims((prev) => prev.map((c) => (c.id === id ? { ...c, ...data } : c)));
  };

  const startEdit = () => {
    if (!selected) return;
    setEditId(selected.id);
    setDraft({ ...selected });
  };

  const saveEdit = () => {
    if (!editId) return;
    updateClaim(editId, draft);
    setEditId(null);
    setDraft({});
  };

  const cancelEdit = () => {
    setEditId(null);
    setDraft({});
  };

  const handleDelete = () => {
    if (!selectedId) return;
    if (window.confirm(`클레임 ${selectedId}를 삭제하시겠습니까?`)) {
      setClaims((prev) => prev.filter((c) => c.id !== selectedId));
      setSelectedId(null);
    }
  };

  const isEditing = (id: string) => editId === id;

  return (
    <div className="space-y-4">
      <div className="rounded-lg border border-background-200/70 bg-primary-800 px-6 py-3 text-center">
        <h2 className="text-sm font-medium text-background-50">
          AD-02 (에이전시 / 손해사정 담당자)
        </h2>
      </div>

      <div className="rounded-lg border border-background-200/70 bg-background-50">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-background-200/70 bg-background-100">
                <th className="px-2 py-2 text-center w-8">
                  <input type="checkbox" className="accent-primary-500" disabled />
                </th>
                <th className="px-2 py-2 font-medium text-foreground-600">순번</th>
                <th className="px-2 py-2 font-medium text-foreground-600">피해자명</th>
                <th className="px-2 py-2 font-medium text-foreground-600">전화번호</th>
                <th className="px-2 py-2 font-medium text-foreground-600">시/도</th>
                <th className="px-2 py-2 font-medium text-foreground-600">접수일자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">사고일자</th>
                <th className="px-2 py-2 font-medium text-foreground-600">사고유형</th>
                <th className="px-2 py-2 font-medium text-foreground-600">경위</th>
                <th className="px-2 py-2 font-medium text-foreground-600">호텔</th>
                <th className="px-2 py-2 font-medium text-foreground-600">지점</th>
                <th className="px-2 py-2 font-medium text-foreground-600">손사배정</th>
                <th className="px-2 py-2 font-medium text-foreground-600">손사 담당자정보</th>
                <th className="px-2 py-2 font-medium text-foreground-600">보험사</th>
                <th className="px-2 py-2 font-medium text-foreground-600">접수메일</th>
                <th className="px-2 py-2 font-medium text-foreground-600">상태</th>
              </tr>
            </thead>
            <tbody>
              {claims.map((c) => {
                const editing = isEditing(c.id);
                return (
                  <tr
                    key={c.id}
                    className={`border-b border-background-200/60 hover:bg-background-100/60 ${
                      selectedId === c.id ? "bg-accent-50/40" : ""
                    }`}
                    onClick={() => setSelectedId(c.id)}
                  >
                    <td className="px-2 py-2 text-center">
                      <input
                        type="radio"
                        name="claim-select"
                        checked={selectedId === c.id}
                        onChange={() => setSelectedId(c.id)}
                        className="accent-primary-500 cursor-pointer"
                      />
                    </td>
                    <td className="px-2 py-2">{c.seq}</td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-20 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.victimName ?? c.victimName}
                          onChange={(e) => setDraft({ ...draft, victimName: e.target.value })}
                        />
                      ) : (
                        c.victimName
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.phone ?? c.phone}
                          onChange={(e) => setDraft({ ...draft, phone: e.target.value })}
                        />
                      ) : (
                        c.phone
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.city ?? c.city}
                          onChange={(e) => setDraft({ ...draft, city: e.target.value })}
                        />
                      ) : (
                        c.city
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          type="date"
                          className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.receivedAt ?? c.receivedAt}
                          onChange={(e) => setDraft({ ...draft, receivedAt: e.target.value })}
                        />
                      ) : (
                        c.receivedAt
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          type="date"
                          className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.occurredAt ?? c.occurredAt}
                          onChange={(e) => setDraft({ ...draft, occurredAt: e.target.value })}
                        />
                      ) : (
                        c.occurredAt
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-20 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.category ?? c.category}
                          onChange={(e) => setDraft({ ...draft, category: e.target.value })}
                        />
                      ) : (
                        c.category
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.details ?? c.details}
                          onChange={(e) => setDraft({ ...draft, details: e.target.value })}
                        />
                      ) : (
                        c.details
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-16 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.hotel ?? c.hotel}
                          onChange={(e) => setDraft({ ...draft, hotel: e.target.value })}
                        />
                      ) : (
                        c.hotel
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-16 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.branch ?? c.branch}
                          onChange={(e) => setDraft({ ...draft, branch: e.target.value })}
                        />
                      ) : (
                        c.branch
                      )}
                    </td>
                    <td className="px-2 py-2" onClick={(e) => e.stopPropagation()}>
                      <select
                        className="w-24 rounded border border-background-300 px-1 py-0.5 text-xs text-accent-700 cursor-pointer"
                        value={editing ? (draft.assignedInsurer ?? c.assignedInsurer) : c.assignedInsurer}
                        onChange={(e) => {
                          if (editing) {
                            setDraft({ ...draft, assignedInsurer: e.target.value });
                          } else {
                            updateClaim(c.id, { assignedInsurer: e.target.value });
                          }
                        }}
                      >
                        {insurerAssignOptions.map((o) => (
                          <option key={o} value={o}>
                            {o}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-16 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.assignedContact ?? c.assignedContact}
                          onChange={(e) => setDraft({ ...draft, assignedContact: e.target.value })}
                        />
                      ) : (
                        <span className="text-accent-700">{c.assignedContact}</span>
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <div className="flex flex-col gap-1">
                          <input
                            className="w-20 rounded border border-background-300 px-1 py-0.5 text-xs"
                            value={draft.insuranceCompany ?? c.insuranceCompany}
                            onChange={(e) => setDraft({ ...draft, insuranceCompany: e.target.value })}
                          />
                          <input
                            className="w-20 rounded border border-background-300 px-1 py-0.5 text-xs"
                            value={draft.insurancePhone ?? c.insurancePhone}
                            onChange={(e) => setDraft({ ...draft, insurancePhone: e.target.value })}
                            placeholder="전화번호"
                          />
                        </div>
                      ) : (
                        <div className="flex flex-col gap-0.5">
                          <span>{c.insuranceCompany}</span>
                          <span className="text-accent-700">{c.insurancePhone}</span>
                        </div>
                      )}
                    </td>
                    <td className="px-2 py-2">
                      {editing ? (
                        <input
                          className="w-28 rounded border border-background-300 px-1 py-0.5 text-xs"
                          value={draft.email ?? c.email}
                          onChange={(e) => setDraft({ ...draft, email: e.target.value })}
                        />
                      ) : (
                        <a href={`mailto:${c.email}`} className="text-blue-600 hover:underline">
                          {c.email}
                        </a>
                      )}
                    </td>
                    <td className="px-2 py-2">
                      <span
                        className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${
                          statusBadge[c.status] ?? statusBadge["접수"]
                        }`}
                      >
                        {c.status}
                      </span>
                    </td>
                  </tr>
                );
              })}
              {claims.length === 0 && (
                <tr>
                  <td colSpan={16} className="px-5 py-10 text-center text-sm text-foreground-500">
                    등록된 클레임이 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-center gap-3 border-t border-background-200/70 px-5 py-5">
          {editId ? (
            <>
              <button
                onClick={saveEdit}
                className="rounded border border-primary-300 bg-primary-50 px-6 py-2 text-sm font-medium text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
              >
                저장하기
              </button>
              <button
                onClick={cancelEdit}
                className="rounded border border-background-300 bg-background-50 px-6 py-2 text-sm text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                취소
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => {
                  if (!selected) {
                    alert("행을 선택해 주세요.");
                    return;
                  }
                  alert("데이터가 저장되었습니다.");
                }}
                className="rounded border border-primary-300 bg-primary-50 px-6 py-2 text-sm font-medium text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
              >
                저장하기
              </button>
              <button
                onClick={() => {
                  if (!selected) {
                    alert("수정할 행을 선택해 주세요.");
                    return;
                  }
                  startEdit();
                }}
                className="rounded border border-background-300 bg-background-50 px-6 py-2 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                수정하기
              </button>
              <button
                onClick={handleDelete}
                className="rounded border border-accent-300 bg-accent-50 px-6 py-2 text-sm text-accent-700 hover:bg-accent-100 cursor-pointer whitespace-nowrap"
              >
                삭제하기
              </button>
            </>
          )}
        </div>
      </div>

      {selected && !editId && (
        <div className="rounded-lg border border-background-200/70 bg-background-50 p-5">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs tracking-[0.18em] uppercase text-foreground-500">
                Selected Claim
              </div>
              <h3 className="mt-1 font-heading text-lg font-semibold">
                {selected.id} · {selected.victimName}
              </h3>
            </div>
            <span
              className={`rounded-full px-3 py-1 text-xs ${
                statusBadge[selected.status] ?? statusBadge["접수"]
              }`}
            >
              {selected.status}
            </span>
          </div>
          <div className="mt-4 grid grid-cols-2 gap-4 text-sm md:grid-cols-4">
            <div>
              <div className="text-xs text-foreground-500">피해자</div>
              <div className="mt-0.5 font-medium">{selected.victimName}</div>
            </div>
            <div>
              <div className="text-xs text-foreground-500">연락처</div>
              <div className="mt-0.5 font-medium">{selected.phone}</div>
            </div>
            <div>
              <div className="text-xs text-foreground-500">사고유형</div>
              <div className="mt-0.5 font-medium">{selected.category}</div>
            </div>
            <div>
              <div className="text-xs text-foreground-500">지점</div>
              <div className="mt-0.5 font-medium">{selected.branch}</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

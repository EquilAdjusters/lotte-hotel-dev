import { useEffect, useState } from "react";
import axios from "axios";
import FormField from "@/shared/components/FormField";
import {
  activateBranchGroup,
  addBranchGroupMember,
  createBranchGroup,
  deactivateBranchGroup,
  fetchBranchGroupMembers,
  fetchBranchGroups,
  removeBranchGroupMember,
  updateBranchGroup,
} from "@/entities/branch-group/api/branchGroupApi";
import type {
  BranchGroupMember,
  BranchGroupOption,
} from "@/entities/branch-group/model/types";
import { fetchBranches } from "@/entities/branch/api/branchApi";
import type { BranchOption } from "@/entities/branch/model/types";

function errorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const message = (err.response?.data as { message?: string } | undefined)?.message;
    if (message) return message;
  }
  return fallback;
}

export default function BranchGroupSection() {
  const [groups, setGroups] = useState<BranchGroupOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const selected = groups.find((g) => g.id === selectedId) ?? null;
  const [form, setForm] = useState<BranchGroupOption | "new" | null>(null);
  const [busyId, setBusyId] = useState<number | null>(null);

  const reload = () => setReloadToken((t) => t + 1);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setLoading(true);
      setError(null);
    });

    fetchBranchGroups(false)
      .then((data) => {
        if (!cancelled) setGroups(data);
      })
      .catch(() => {
        if (!cancelled) setError("권역그룹 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [reloadToken]);

  const handleToggleActive = async (group: BranchGroupOption) => {
    if (group.active && !confirm(`${group.name} 권역그룹을 사용 중지하시겠습니까?`)) return;
    setBusyId(group.id);
    try {
      if (group.active) {
        await deactivateBranchGroup(group.id);
      } else {
        await activateBranchGroup(group.id);
      }
      reload();
    } catch (err) {
      alert(errorMessage(err, "처리 중 오류가 발생했습니다."));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">권역그룹 관리</h2>
        <button
          type="button"
          onClick={() => setForm("new")}
          className="rounded-md bg-primary-500 px-4 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
        >
          <span className="mr-1">
            <i className="ri-add-line"></i>
          </span>
          권역그룹 추가
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-[280px_1fr] divide-y lg:divide-y-0 lg:divide-x divide-background-200/70">
        <div className="p-4">
          {loading && <div className="py-6 text-center text-xs text-foreground-500">불러오는 중...</div>}
          {!loading && error && <div className="py-6 text-center text-xs text-accent-700">{error}</div>}
          {!loading && !error && groups.length === 0 && (
            <div className="py-6 text-center text-xs text-foreground-500">등록된 권역그룹이 없습니다.</div>
          )}
          {!loading && !error && (
            <ul className="space-y-1">
              {groups.map((g) => (
                <li
                  key={g.id}
                  onClick={() => setSelectedId(g.id)}
                  className={`flex items-center justify-between rounded px-2 py-1.5 text-sm cursor-pointer ${
                    selected?.id === g.id
                      ? "bg-primary-50 text-primary-800"
                      : "hover:bg-background-100"
                  }`}
                >
                  <span className={g.active ? "" : "text-foreground-400 line-through"}>
                    {g.name}
                  </span>
                  <div className="flex gap-1">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setForm(g);
                      }}
                      className="text-[11px] text-foreground-500 hover:text-primary-600 cursor-pointer"
                    >
                      수정
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleToggleActive(g);
                      }}
                      disabled={busyId === g.id}
                      className="text-[11px] text-foreground-500 hover:text-primary-600 disabled:opacity-50 cursor-pointer"
                    >
                      {g.active ? "중지" : "재활성화"}
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="p-4">
          {selected ? (
            <MemberManager group={selected} />
          ) : (
            <div className="py-6 text-center text-xs text-foreground-500">
              좌측에서 권역그룹을 선택하면 소속 지점을 관리할 수 있습니다.
            </div>
          )}
        </div>
      </div>

      {form && (
        <BranchGroupFormModal
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

function BranchGroupFormModal({
  target,
  onClose,
  onSaved,
}: {
  target: BranchGroupOption | "new";
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
      setError("권역그룹명을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      if (isNew) {
        await createBranchGroup(name.trim());
      } else if (existing) {
        await updateBranchGroup(existing.id, name.trim());
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
          {isNew ? "권역그룹 추가" : `권역그룹 수정 · ${existing?.name}`}
        </h3>
        <div className="mt-4">
          <FormField label="권역그룹명">
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

function MemberManager({ group }: { group: BranchGroupOption }) {
  const [members, setMembers] = useState<BranchGroupMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  const [allBranches, setAllBranches] = useState<BranchOption[]>([]);
  const [addBranchId, setAddBranchId] = useState<number | "">("");
  const [busy, setBusy] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const reload = () => setReloadToken((t) => t + 1);

  useEffect(() => {
    let cancelled = false;

    queueMicrotask(() => {
      if (cancelled) return;
      setLoading(true);
      setError(null);
    });

    fetchBranchGroupMembers(group.id)
      .then((data) => {
        if (!cancelled) setMembers(data);
      })
      .catch(() => {
        if (!cancelled) setError("소속 지점을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [group.id, reloadToken]);

  useEffect(() => {
    fetchBranches().then(setAllBranches).catch(() => setAllBranches([]));
  }, []);

  const availableBranches = allBranches.filter(
    (b) => !members.some((m) => m.branchId === b.id)
  );

  const handleAdd = async () => {
    if (addBranchId === "") return;
    setBusy(true);
    setActionError(null);
    try {
      await addBranchGroupMember(group.id, addBranchId);
      setAddBranchId("");
      reload();
    } catch (err) {
      setActionError(errorMessage(err, "지점 추가 중 오류가 발생했습니다."));
    } finally {
      setBusy(false);
    }
  };

  const handleRemove = async (member: BranchGroupMember) => {
    setBusy(true);
    setActionError(null);
    try {
      await removeBranchGroupMember(group.id, member.branchId);
      reload();
    } catch (err) {
      setActionError(errorMessage(err, "지점 해제 중 오류가 발생했습니다."));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <h3 className="mb-2 text-sm font-semibold text-foreground-800">
        {group.name} · 소속 지점
      </h3>

      <div className="mb-3 flex items-center gap-2">
        <select
          value={addBranchId}
          onChange={(e) => setAddBranchId(e.target.value ? Number(e.target.value) : "")}
          className="flex-1 rounded-md border border-background-300/60 px-3 py-2 text-sm outline-none cursor-pointer"
        >
          <option value="">지점 선택</option>
          {availableBranches.map((b) => (
            <option key={b.id} value={b.id}>
              {b.hotelName} / {b.name}
            </option>
          ))}
        </select>
        <button
          type="button"
          onClick={handleAdd}
          disabled={addBranchId === "" || busy}
          className="rounded-md bg-primary-500 px-4 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-60 cursor-pointer whitespace-nowrap"
        >
          추가
        </button>
      </div>

      {actionError && <p className="mb-2 text-xs text-accent-700">{actionError}</p>}

      {loading && <div className="py-4 text-center text-xs text-foreground-500">불러오는 중...</div>}
      {!loading && error && <div className="py-4 text-center text-xs text-accent-700">{error}</div>}
      {!loading && !error && members.length === 0 && (
        <div className="py-4 text-center text-xs text-foreground-500">소속된 지점이 없습니다.</div>
      )}
      {!loading && !error && members.length > 0 && (
        <ul className="space-y-1">
          {members.map((m) => (
            <li
              key={m.id}
              className="flex items-center justify-between rounded border border-background-200/70 px-3 py-2 text-sm"
            >
              <span>{m.branchName}</span>
              <button
                onClick={() => handleRemove(m)}
                disabled={busy}
                className="text-xs text-accent-700 hover:text-accent-800 disabled:opacity-50 cursor-pointer"
              >
                해제
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

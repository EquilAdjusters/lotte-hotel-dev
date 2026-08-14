import { useState } from "react";
import AppShell from "@/app/layouts/AppShell";
import {
  sampleAdminAccounts,
  sampleInsurerCompanies,
  type AdminAccount,
  type InsurerCompany,
} from "@/shared/constants/sample-admin";

export default function AdminAccountsPage() {
  return (
    <AppShell>
      <AdminAccountsContent />
    </AppShell>
  );
}

function AdminAccountsContent() {
  return (
    <div className="space-y-8">
      <div className="rounded-lg border border-background-200/70 bg-primary-800 px-6 py-3 text-center">
        <h2 className="text-sm font-medium text-background-50">
          AD-01 (에이전시 담당자)
        </h2>
      </div>

      <AccountSection />
      <InsurerSection />
    </div>
  );
}

/* ─── 계정현황 ─── */
function AccountSection() {
  const [accounts, setAccounts] = useState<AdminAccount[]>(sampleAdminAccounts);
  const [editId, setEditId] = useState<string | null>(null);
  const [draft, setDraft] = useState<Partial<AdminAccount>>({});
  const [showAddMajor, setShowAddMajor] = useState(false);
  const [showAddMinor, setShowAddMinor] = useState(false);
  const [newMajor, setNewMajor] = useState({ category: "", loginId: "", password: "", insurer: "", email: "" });
  const [newMinor, setNewMinor] = useState({ category: "", loginId: "", password: "", insurer: "", email: "" });

  const startEdit = (a: AdminAccount) => {
    setEditId(a.id);
    setDraft({ ...a });
  };

  const saveEdit = () => {
    if (!editId) return;
    setAccounts((prev) => prev.map((a) => (a.id === editId ? { ...a, ...draft } : a)));
    setEditId(null);
    setDraft({});
  };

  const cancelEdit = () => {
    setEditId(null);
    setDraft({});
  };

  const deleteAccount = (id: string) => {
    setAccounts((prev) => prev.filter((a) => a.id !== id));
  };

  const handleAddMajor = () => {
    if (!newMajor.category.trim() || !newMajor.loginId.trim()) return;
    setAccounts((prev) => [
      ...prev,
      {
        id: `acc-${Date.now()}`,
        category: newMajor.category,
        type: "major",
        loginId: newMajor.loginId,
        password: newMajor.password || "0000",
        insurer: newMajor.insurer || "롯데손보",
        email: newMajor.email || "CLAIM@LOTTEINS.COM",
      },
    ]);
    setNewMajor({ category: "", loginId: "", password: "", insurer: "", email: "" });
    setShowAddMajor(false);
  };

  const handleAddMinor = () => {
    if (!newMinor.category.trim() || !newMinor.loginId.trim()) return;
    setAccounts((prev) => [
      ...prev,
      {
        id: `acc-${Date.now()}`,
        category: newMinor.category,
        type: "minor",
        loginId: newMinor.loginId,
        password: newMinor.password || "0000",
        insurer: newMinor.insurer || "롯데손보",
        email: newMinor.email || "CLAIM@LOTTEINS.COM",
      },
    ]);
    setNewMinor({ category: "", loginId: "", password: "", insurer: "", email: "" });
    setShowAddMinor(false);
  };

  const majorList = accounts.filter((a) => a.type === "major");
  const minorList = accounts.filter((a) => a.type === "minor");

  const renderRow = (a: AdminAccount, indent = false) => {
    const isEdit = editId === a.id;
    return (
      <tr key={a.id} className="border-b border-background-200/60 hover:bg-background-100/60">
        <td className={`px-3 py-2 text-sm ${indent ? "pl-8" : ""}`}>
          {isEdit ? (
            <input
              className="w-full rounded border border-background-300 px-2 py-1 text-sm"
              value={draft.category ?? a.category}
              onChange={(e) => setDraft({ ...draft, category: e.target.value })}
            />
          ) : (
            a.category
          )}
        </td>
        <td className="px-3 py-2 text-sm">
          {isEdit ? (
            <input
              className="w-full rounded border border-background-300 px-2 py-1 text-sm"
              value={draft.loginId ?? a.loginId}
              onChange={(e) => setDraft({ ...draft, loginId: e.target.value })}
            />
          ) : (
            a.loginId
          )}
        </td>
        <td className="px-3 py-2 text-sm">
          {isEdit ? (
            <input
              className="w-full rounded border border-background-300 px-2 py-1 text-sm"
              value={draft.password ?? a.password}
              onChange={(e) => setDraft({ ...draft, password: e.target.value })}
            />
          ) : (
            a.password
          )}
        </td>
        <td className="px-3 py-2 text-sm">
          {isEdit ? (
            <input
              className="w-full rounded border border-background-300 px-2 py-1 text-sm"
              value={draft.insurer ?? a.insurer}
              onChange={(e) => setDraft({ ...draft, insurer: e.target.value })}
            />
          ) : (
            a.insurer
          )}
        </td>
        <td className="px-3 py-2 text-sm">
          {isEdit ? (
            <input
              className="w-full rounded border border-background-300 px-2 py-1 text-sm"
              value={draft.email ?? a.email}
              onChange={(e) => setDraft({ ...draft, email: e.target.value })}
            />
          ) : (
            <a href={`mailto:${a.email}`} className="text-blue-600 hover:underline">
              {a.email}
            </a>
          )}
        </td>
        <td className="px-3 py-2 text-sm">
          {isEdit ? (
            <div className="flex gap-1">
              <button
                onClick={saveEdit}
                className="rounded border border-primary-300 bg-primary-50 px-2 py-1 text-xs text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
              >
                저장
              </button>
              <button
                onClick={cancelEdit}
                className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                취소
              </button>
            </div>
          ) : (
            <div className="flex gap-1">
              <button
                onClick={() => startEdit(a)}
                className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                수정하기
              </button>
              <button
                onClick={() => deleteAccount(a.id)}
                className="rounded border border-accent-300 bg-accent-50 px-2 py-1 text-xs text-accent-700 hover:bg-accent-100 cursor-pointer whitespace-nowrap"
              >
                삭제하기
              </button>
            </div>
          )}
        </td>
      </tr>
    );
  };

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">계정현황</h2>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-background-200/70 bg-background-100">
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">분류</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">ID</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">PW</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">보험사</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">메일주소</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">관리</th>
            </tr>
          </thead>
          <tbody>
            {majorList.map((a) => renderRow(a))}
            {minorList.map((a) => renderRow(a, true))}
          </tbody>
        </table>
      </div>

      <div className="flex flex-wrap items-center gap-2 border-t border-background-200/70 px-5 py-4">
        <button
          onClick={() => setShowAddMajor((v) => !v)}
          className="rounded border border-background-300 bg-background-50 px-3 py-2 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          대분류 추가
        </button>
        <button
          onClick={() => setShowAddMinor((v) => !v)}
          className="rounded border border-background-300 bg-background-50 px-3 py-2 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          소분류 추가
        </button>
      </div>

      {showAddMajor && (
        <div className="border-t border-background-200/70 px-5 py-4 bg-background-100/50">
          <div className="mb-2 text-sm font-medium text-foreground-700">대분류 추가</div>
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-5">
            <input
              placeholder="분류명"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMajor.category}
              onChange={(e) => setNewMajor({ ...newMajor, category: e.target.value })}
            />
            <input
              placeholder="ID"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMajor.loginId}
              onChange={(e) => setNewMajor({ ...newMajor, loginId: e.target.value })}
            />
            <input
              placeholder="PW"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMajor.password}
              onChange={(e) => setNewMajor({ ...newMajor, password: e.target.value })}
            />
            <input
              placeholder="보험사"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMajor.insurer}
              onChange={(e) => setNewMajor({ ...newMajor, insurer: e.target.value })}
            />
            <input
              placeholder="메일주소"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMajor.email}
              onChange={(e) => setNewMajor({ ...newMajor, email: e.target.value })}
            />
          </div>
          <div className="mt-3 flex gap-2">
            <button
              onClick={handleAddMajor}
              className="rounded border border-primary-300 bg-primary-50 px-4 py-2 text-sm text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
            >
              저장하기
            </button>
            <button
              onClick={() => setShowAddMajor(false)}
              className="rounded border border-background-300 bg-background-50 px-4 py-2 text-sm text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
            >
              취소
            </button>
          </div>
        </div>
      )}

      {showAddMinor && (
        <div className="border-t border-background-200/70 px-5 py-4 bg-background-100/50">
          <div className="mb-2 text-sm font-medium text-foreground-700">소분류 추가</div>
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-5">
            <input
              placeholder="분류명 (예: 위탁점)"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMinor.category}
              onChange={(e) => setNewMinor({ ...newMinor, category: e.target.value })}
            />
            <input
              placeholder="ID"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMinor.loginId}
              onChange={(e) => setNewMinor({ ...newMinor, loginId: e.target.value })}
            />
            <input
              placeholder="PW"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMinor.password}
              onChange={(e) => setNewMinor({ ...newMinor, password: e.target.value })}
            />
            <input
              placeholder="보험사"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMinor.insurer}
              onChange={(e) => setNewMinor({ ...newMinor, insurer: e.target.value })}
            />
            <input
              placeholder="메일주소"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newMinor.email}
              onChange={(e) => setNewMinor({ ...newMinor, email: e.target.value })}
            />
          </div>
          <div className="mt-3 flex gap-2">
            <button
              onClick={handleAddMinor}
              className="rounded border border-primary-300 bg-primary-50 px-4 py-2 text-sm text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
            >
              저장하기
            </button>
            <button
              onClick={() => setShowAddMinor(false)}
              className="rounded border border-background-300 bg-background-50 px-4 py-2 text-sm text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
            >
              취소
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

/* ─── 약사현황 ─── */
function InsurerSection() {
  const [insurers, setInsurers] = useState<InsurerCompany[]>(sampleInsurerCompanies);
  const [editId, setEditId] = useState<string | null>(null);
  const [draft, setDraft] = useState<Partial<InsurerCompany>>({});
  const [showAdd, setShowAdd] = useState(false);
  const [newItem, setNewItem] = useState({ category: "", name: "", bizNumber: "" });

  const startEdit = (i: InsurerCompany) => {
    setEditId(i.id);
    setDraft({ ...i });
  };

  const saveEdit = () => {
    if (!editId) return;
    setInsurers((prev) => prev.map((i) => (i.id === editId ? { ...i, ...draft } : i)));
    setEditId(null);
    setDraft({});
  };

  const cancelEdit = () => {
    setEditId(null);
    setDraft({});
  };

  const deleteInsurer = (id: string) => {
    setInsurers((prev) => prev.filter((i) => i.id !== id));
  };

  const handleAdd = () => {
    if (!newItem.category.trim() || !newItem.name.trim()) return;
    setInsurers((prev) => [
      ...prev,
      {
        id: `ins-${Date.now()}`,
        category: newItem.category,
        name: newItem.name,
        bizNumber: newItem.bizNumber || "000-00-0000",
      },
    ]);
    setNewItem({ category: "", name: "", bizNumber: "" });
    setShowAdd(false);
  };

  return (
    <div className="rounded-lg border border-background-200/70 bg-background-50">
      <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
        <h2 className="font-heading text-lg font-semibold">약사현황</h2>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-background-200/70 bg-background-100">
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">분류</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">업체명</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">사업자번호</th>
              <th className="px-3 py-2 text-xs font-medium text-foreground-600">관리</th>
            </tr>
          </thead>
          <tbody>
            {insurers.map((i) => {
              const isEdit = editId === i.id;
              return (
                <tr key={i.id} className="border-b border-background-200/60 hover:bg-background-100/60">
                  <td className="px-3 py-2 text-sm">
                    {isEdit ? (
                      <input
                        className="w-full rounded border border-background-300 px-2 py-1 text-sm"
                        value={draft.category ?? i.category}
                        onChange={(e) => setDraft({ ...draft, category: e.target.value })}
                      />
                    ) : (
                      i.category
                    )}
                  </td>
                  <td className="px-3 py-2 text-sm">
                    {isEdit ? (
                      <input
                        className="w-full rounded border border-background-300 px-2 py-1 text-sm"
                        value={draft.name ?? i.name}
                        onChange={(e) => setDraft({ ...draft, name: e.target.value })}
                      />
                    ) : (
                      <span className="flex items-center gap-1">
                        <span className="w-4 h-4 flex items-center justify-center text-foreground-400">
                          <i className="ri-circle-fill text-[6px]"></i>
                        </span>
                        {i.name}
                      </span>
                    )}
                  </td>
                  <td className="px-3 py-2 text-sm">
                    {isEdit ? (
                      <input
                        className="w-full rounded border border-background-300 px-2 py-1 text-sm"
                        value={draft.bizNumber ?? i.bizNumber}
                        onChange={(e) => setDraft({ ...draft, bizNumber: e.target.value })}
                      />
                    ) : (
                      i.bizNumber
                    )}
                  </td>
                  <td className="px-3 py-2 text-sm">
                    {isEdit ? (
                      <div className="flex gap-1">
                        <button
                          onClick={saveEdit}
                          className="rounded border border-primary-300 bg-primary-50 px-2 py-1 text-xs text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
                        >
                          저장
                        </button>
                        <button
                          onClick={cancelEdit}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                        >
                          취소
                        </button>
                      </div>
                    ) : (
                      <div className="flex gap-1">
                        <button
                          onClick={() => startEdit(i)}
                          className="rounded border border-background-300 bg-background-50 px-2 py-1 text-xs text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                        >
                          수정하기
                        </button>
                        <button
                          onClick={() => deleteInsurer(i.id)}
                          className="rounded border border-accent-300 bg-accent-50 px-2 py-1 text-xs text-accent-700 hover:bg-accent-100 cursor-pointer whitespace-nowrap"
                        >
                          삭제하기
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div className="flex flex-wrap items-center gap-2 border-t border-background-200/70 px-5 py-4">
        <button
          onClick={() => setShowAdd((v) => !v)}
          className="rounded border border-background-300 bg-background-50 px-3 py-2 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
        >
          추가하기
        </button>
      </div>

      {showAdd && (
        <div className="border-t border-background-200/70 px-5 py-4 bg-background-100/50">
          <div className="mb-2 text-sm font-medium text-foreground-700">약사 추가</div>
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-4">
            <input
              placeholder="분류"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newItem.category}
              onChange={(e) => setNewItem({ ...newItem, category: e.target.value })}
            />
            <input
              placeholder="업체명"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newItem.name}
              onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
            />
            <input
              placeholder="사업자번호"
              className="rounded border border-background-300 px-3 py-2 text-sm"
              value={newItem.bizNumber}
              onChange={(e) => setNewItem({ ...newItem, bizNumber: e.target.value })}
            />
            <div className="flex gap-2">
              <button
                onClick={handleAdd}
                className="rounded border border-primary-300 bg-primary-50 px-4 py-2 text-sm text-primary-700 hover:bg-primary-100 cursor-pointer whitespace-nowrap"
              >
                저장하기
              </button>
              <button
                onClick={() => setShowAdd(false)}
                className="rounded border border-background-300 bg-background-50 px-4 py-2 text-sm text-foreground-600 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

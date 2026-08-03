import { useMemo, useState } from "react";
import AppShell from "@/app/layouts/AppShell";
import {
  sampleIncidents,
  severityBadge,
  statusBadge,
  type IncidentStatus,
  type SampleIncident,
} from "@/shared/constants/sample-incidents";

const statusList: IncidentStatus[] = ["접수", "검토중", "처리중", "완료", "반려"];
const severityList = ["전체", "낮음", "보통", "높음", "긴급"];

export default function ClaimListPage() {
  return (
    <AppShell>
      <ClaimListContent />
    </AppShell>
  );
}

function ClaimListContent() {
  const [incidents, setIncidents] = useState<SampleIncident[]>(sampleIncidents);
  const [query, setQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState<"전체" | IncidentStatus>("전체");
  const [severityFilter, setSeverityFilter] = useState<string>("전체");
  const [selected, setSelected] = useState<SampleIncident | null>(null);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    return incidents
      .filter((i) => {
        if (statusFilter !== "전체" && i.status !== statusFilter) return false;
        if (severityFilter !== "전체" && i.severity !== severityFilter) return false;
        if (!q) return true;
        return [i.id, i.location, i.category, i.description, i.reporter]
          .join(" ")
          .toLowerCase()
          .includes(q);
      })
      .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1));
  }, [incidents, query, statusFilter, severityFilter]);

  const counts = useMemo(() => {
    const map: Record<string, number> = { 전체: incidents.length };
    for (const s of statusList) {
      map[s] = incidents.filter((i) => i.status === s).length;
    }
    return map;
  }, [incidents]);

  const updateStatus = (id: string, status: IncidentStatus) => {
    setIncidents((prev) => prev.map((i) => (i.id === id ? { ...i, status } : i)));
    setSelected((prev) => (prev && prev.id === id ? { ...prev, status } : prev));
  };

  return (
    <div className="space-y-6">
      <div className="rounded-lg border border-background-200/70 bg-background-50 p-5">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex flex-1 items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3">
            <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
              <i className="ri-search-line"></i>
            </span>
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="클레임 번호, 장소, 유형, 접수자로 검색"
              className="w-full bg-transparent py-2.5 text-sm outline-none placeholder:text-foreground-400"
            />
            {query && (
              <button
                type="button"
                onClick={() => setQuery("")}
                className="w-5 h-5 flex items-center justify-center text-foreground-500 hover:text-foreground-800 cursor-pointer"
              >
                <i className="ri-close-line"></i>
              </button>
            )}
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <span className="text-xs text-foreground-500">심각도</span>
            <div className="flex flex-wrap gap-1 rounded-full bg-background-100 p-1">
              {severityList.map((s) => (
                <button
                  type="button"
                  key={s}
                  onClick={() => setSeverityFilter(s)}
                  className={`rounded-full px-3 py-1 text-xs cursor-pointer whitespace-nowrap ${
                    severityFilter === s
                      ? "bg-background-50 text-foreground-900 shadow-sm"
                      : "text-foreground-600 hover:text-foreground-900"
                  }`}
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="mt-4 flex flex-wrap gap-2">
          {(["전체", ...statusList] as const).map((s) => (
            <button
              type="button"
              key={s}
              onClick={() => setStatusFilter(s)}
              className={`flex items-center gap-2 rounded-md border px-3 py-1.5 text-sm cursor-pointer whitespace-nowrap ${
                statusFilter === s
                  ? "border-primary-500 bg-primary-500 text-background-50"
                  : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
              }`}
            >
              {s}
              <span
                className={`rounded-full px-1.5 py-0.5 text-[11px] ${
                  statusFilter === s
                    ? "bg-background-50/20 text-background-50"
                    : "bg-background-100 text-foreground-600"
                }`}
              >
                {counts[s] ?? 0}
              </span>
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1.6fr_1fr]">
        <div className="rounded-lg border border-background-200/70 bg-background-50 overflow-hidden">
          <div className="hidden md:grid grid-cols-[140px_1fr_120px_110px_140px] gap-2 border-b border-background-200/70 bg-background-100 px-5 py-3 text-[11px] tracking-widest uppercase text-foreground-500">
            <span>번호</span>
            <span>내용</span>
            <span>심각도</span>
            <span>상태</span>
            <span>접수 시간</span>
          </div>
          <ul className="divide-y divide-background-200/70">
            {filtered.map((i) => {
              const isSelected = selected?.id === i.id;
              return (
                <li
                  key={i.id}
                  onClick={() => setSelected(i)}
                  className={`grid cursor-pointer grid-cols-1 gap-2 px-5 py-4 transition md:grid-cols-[140px_1fr_120px_110px_140px] md:items-center ${
                    isSelected ? "bg-primary-50" : "hover:bg-background-100"
                  }`}
                >
                  <div className="font-mono text-xs text-foreground-600">{i.id}</div>
                  <div className="min-w-0">
                    <div className="truncate text-sm font-medium">
                      [{i.category}] {i.location}
                    </div>
                    <div className="truncate text-xs text-foreground-500">
                      {i.description}
                    </div>
                  </div>
                  <div>
                    <span
                      className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[11px] ${
                        severityBadge[i.severity] ?? severityBadge["보통"]
                      }`}
                    >
                      {i.severity}
                    </span>
                  </div>
                  <div>
                    <span
                      className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${
                        statusBadge[i.status] ?? statusBadge["접수"]
                      }`}
                    >
                      {i.status}
                    </span>
                  </div>
                  <div className="text-xs text-foreground-500">
                    {new Date(i.createdAt).toLocaleString("ko-KR", {
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </div>
                </li>
              );
            })}
            {filtered.length === 0 && (
              <li className="px-5 py-16 text-center text-sm text-foreground-500">
                조건에 맞는 클레임이 없습니다.
              </li>
            )}
          </ul>
        </div>

        <aside className="rounded-lg border border-background-200/70 bg-background-50">
          {selected ? (
            <DetailPanel
              incident={selected}
              onUpdate={(s) => updateStatus(selected.id, s)}
              onClose={() => setSelected(null)}
            />
          ) : (
            <div className="flex h-full min-h-[400px] flex-col items-center justify-center gap-3 p-10 text-center">
              <span className="w-12 h-12 flex items-center justify-center rounded-full bg-background-100 text-foreground-500">
                <i className="ri-file-search-line text-xl"></i>
              </span>
              <div className="text-sm text-foreground-600">
                왼쪽 목록에서 항목을 선택하면
                <br />
                상세 내용과 상태 변경이 가능합니다.
              </div>
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}

function DetailPanel({
  incident,
  onUpdate,
  onClose,
}: {
  incident: SampleIncident;
  onUpdate: (s: IncidentStatus) => void;
  onClose: () => void;
}) {
  return (
    <div className="flex h-full flex-col">
      <div className="flex items-start justify-between border-b border-background-200/70 px-5 py-4">
        <div>
          <div className="font-mono text-xs text-foreground-500">{incident.id}</div>
          <h3 className="mt-1 font-heading text-lg font-semibold">
            [{incident.category}]
          </h3>
          <div className="mt-1 text-sm text-foreground-600">{incident.location}</div>
        </div>
        <button
          type="button"
          onClick={onClose}
          className="w-8 h-8 flex items-center justify-center rounded-md text-foreground-500 hover:bg-background-100 cursor-pointer"
        >
          <i className="ri-close-line"></i>
        </button>
      </div>

      <div className="flex-1 space-y-5 px-5 py-5">
        <div className="flex items-center gap-2">
          <span
            className={`inline-flex items-center rounded-full border px-2 py-0.5 text-[11px] ${
              severityBadge[incident.severity] ?? severityBadge["보통"]
            }`}
          >
            심각도 · {incident.severity}
          </span>
          <span
            className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${
              statusBadge[incident.status] ?? statusBadge["접수"]
            }`}
          >
            {incident.status}
          </span>
        </div>

        <div>
          <div className="text-[11px] tracking-widest uppercase text-foreground-500">
            상세 내용
          </div>
          <p className="mt-2 whitespace-pre-line text-sm leading-relaxed text-foreground-800">
            {incident.description}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-4 text-sm">
          <InfoRow
            label="발생 일시"
            value={new Date(incident.occurredAt).toLocaleString("ko-KR")}
          />
          <InfoRow
            label="접수 시간"
            value={new Date(incident.createdAt).toLocaleString("ko-KR")}
          />
          <InfoRow label="접수자" value={incident.reporter} />
          <InfoRow label="연락처" value={incident.contact} mono />
        </div>

        <div>
          <div className="text-[11px] tracking-widest uppercase text-foreground-500">
            상태 변경
          </div>
          <div className="mt-2 flex flex-wrap gap-2">
            {statusList.map((s) => (
              <button
                type="button"
                key={s}
                onClick={() => onUpdate(s)}
                className={`rounded-full border px-3 py-1.5 text-xs cursor-pointer whitespace-nowrap ${
                  incident.status === s
                    ? "border-primary-500 bg-primary-500 text-background-50"
                    : "border-background-300/60 bg-background-50 text-foreground-700 hover:border-primary-300"
                }`}
              >
                {s}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function InfoRow({
  label,
  value,
  mono,
}: {
  label: string;
  value: string;
  mono?: boolean;
}) {
  return (
    <div>
      <div className="text-[11px] tracking-widest uppercase text-foreground-500">
        {label}
      </div>
      <div className={`mt-1 text-sm text-foreground-800 ${mono ? "font-mono" : ""}`}>
        {value}
      </div>
    </div>
  );
}

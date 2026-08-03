import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import AppShell from "@/app/layouts/AppShell";
import {
  sampleIncidents,
  severityBadge,
  statusBadge,
} from "@/shared/constants/sample-incidents";

export default function DashboardPage() {
  return (
    <AppShell>
      <Dashboard />
    </AppShell>
  );
}

function Dashboard() {
  const navigate = useNavigate();

  const stats = useMemo(() => {
    const total = sampleIncidents.length;
    const open = sampleIncidents.filter(
      (i) => i.status !== "완료" && i.status !== "반려"
    ).length;
    const urgent = sampleIncidents.filter(
      (i) => i.severity === "긴급" || i.severity === "높음"
    ).length;
    const done = sampleIncidents.filter((i) => i.status === "완료").length;
    const rejected = sampleIncidents.filter((i) => i.status === "반려").length;
    return { total, open, urgent, done, rejected };
  }, []);

  const recent = useMemo(
    () =>
      [...sampleIncidents]
        .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1))
        .slice(0, 5),
    []
  );

  return (
    <div className="space-y-8">
      <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
        {[
          {
            label: "전체 클레임",
            value: stats.total,
            hint: "누적 접수 건수",
            icon: "ri-file-list-3-line",
            tone: "bg-background-100 text-foreground-800",
          },
          {
            label: "처리 대기",
            value: stats.open,
            hint: "접수·검토·처리중",
            icon: "ri-hourglass-2-line",
            tone: "bg-secondary-100 text-secondary-900",
          },
          {
            label: "긴급 · 높음",
            value: stats.urgent,
            hint: "우선 대응 필요",
            icon: "ri-alarm-warning-line",
            tone: "bg-accent-100 text-accent-900",
          },
          {
            label: "처리 완료",
            value: stats.done,
            hint: "접수 종결",
            icon: "ri-check-double-line",
            tone: "bg-primary-500 text-background-50",
          },
          {
            label: "반려",
            value: stats.rejected,
            hint: "면책·기각",
            icon: "ri-close-circle-line",
            tone: "bg-background-200/80 text-foreground-700",
          },
        ].map((card) => (
          <div
            key={card.label}
            className="rounded-lg border border-background-200/70 bg-background-50 p-5"
          >
            <div className="flex items-center justify-between">
              <div className="text-sm text-foreground-600">{card.label}</div>
              <span
                className={`w-9 h-9 flex items-center justify-center rounded-md ${card.tone}`}
              >
                <i className={card.icon}></i>
              </span>
            </div>
            <div className="mt-3 font-heading text-3xl font-semibold">
              {card.value}
            </div>
            <div className="mt-1 text-xs text-foreground-500">{card.hint}</div>
          </div>
        ))}
      </section>

      <section className="grid grid-cols-1 gap-6 lg:grid-cols-[1.4fr_1fr]">
        <div className="rounded-lg border border-background-200/70 bg-background-50">
          <div className="flex items-center justify-between border-b border-background-200/70 px-5 py-4">
            <div>
              <div className="text-xs tracking-[0.18em] uppercase text-foreground-500">
                Latest Reports
              </div>
              <h2 className="mt-1 font-heading text-lg font-semibold">
                최근 접수 내역
              </h2>
            </div>
            <button
              type="button"
              onClick={() => navigate("/claims")}
              className="flex items-center gap-1 text-sm text-foreground-700 hover:text-primary-600 cursor-pointer whitespace-nowrap"
            >
              전체 보기 <i className="ri-arrow-right-line"></i>
            </button>
          </div>
          <ul className="divide-y divide-background-200/70">
            {recent.map((i) => (
              <li key={i.id} className="flex items-center gap-4 px-5 py-4">
                <div className="w-10 h-10 flex items-center justify-center rounded-md bg-background-100 text-foreground-700">
                  <i className="ri-file-text-line"></i>
                </div>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-xs text-foreground-500">
                      {i.id}
                    </span>
                    <span
                      className={`rounded-full border px-2 py-0.5 text-[11px] ${
                        severityBadge[i.severity] ?? severityBadge["보통"]
                      }`}
                    >
                      {i.severity}
                    </span>
                  </div>
                  <div className="mt-1 truncate text-sm font-medium">
                    [{i.category}] {i.location}
                  </div>
                  <div className="truncate text-xs text-foreground-500">
                    {i.description}
                  </div>
                </div>
                <div className="hidden sm:flex flex-col items-end gap-1">
                  <span
                    className={`rounded-full px-2 py-0.5 text-[11px] ${
                      statusBadge[i.status] ?? statusBadge["접수"]
                    }`}
                  >
                    {i.status}
                  </span>
                  <span className="text-[11px] text-foreground-500">
                    {new Date(i.createdAt).toLocaleString("ko-KR", {
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        </div>

        <div className="space-y-6">
          <div className="rounded-lg border border-background-200/70 bg-primary-500 p-6 text-background-50">
            <div className="flex items-center gap-2 text-[11px] tracking-[0.2em] uppercase text-background-50/70">
              <span className="w-4 h-4 flex items-center justify-center rounded-full bg-background-50/20 text-[10px]">
                W
              </span>
              WISE Claim Portal
            </div>
            <h3 className="mt-2 font-heading text-2xl font-semibold">
              안녕하세요, 담당자님
            </h3>
            <p className="mt-2 text-sm text-background-50/85">
              현재 처리 대기 중인 클레임이 <strong>{stats.open}</strong>건입니다.
              신규 사고를 접수하거나 진행 현황을 확인해 보세요.
            </p>
            <div className="mt-6 flex flex-wrap gap-2">
              <button
                type="button"
                onClick={() => navigate("/claims/new")}
                className="rounded-md bg-background-50 px-4 py-2 text-sm font-medium text-primary-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
              >
                <span className="mr-1">
                  <i className="ri-add-line"></i>
                </span>
                사고 접수
              </button>
              <button
                type="button"
                onClick={() => navigate("/claims")}
                className="rounded-md border border-background-50/40 px-4 py-2 text-sm font-medium text-background-50 hover:bg-background-50/10 cursor-pointer whitespace-nowrap"
              >
                현황 조회
              </button>
            </div>
          </div>

          <div className="rounded-lg border border-background-200/70 bg-background-50 p-6">
            <div className="text-xs tracking-[0.2em] uppercase text-foreground-500">
              WISE 처리 프로세스
            </div>
            <h3 className="mt-2 font-heading text-lg font-semibold">
              사고 접수부터 보상까지
            </h3>
            <ol className="mt-4 space-y-4 text-sm">
              {[
                {
                  step: "01",
                  title: "사고 접수",
                  desc: "발생 장소, 유형, 심각도, 상세 내용을 입력하고 클레임 번호를 발급받습니다.",
                  icon: "ri-clipboard-line",
                },
                {
                  step: "02",
                  title: "WISE 심사역 검토",
                  desc: "WISE 담당자가 접수 건을 검토하고 보험 약관에 따라 심사역을 진행합니다.",
                  icon: "ri-search-eye-line",
                },
                {
                  step: "03",
                  title: "처리 완료 및 보상",
                  desc: "심사 결과에 따라 보상이 이루어지고, 처리 완료 후 이력이 기록됩니다.",
                  icon: "ri-check-double-line",
                },
              ].map((s) => (
                <li key={s.step} className="flex gap-3">
                  <span className="mt-0.5 w-9 h-9 flex items-center justify-center rounded-md bg-accent-100 text-accent-900">
                    <i className={s.icon}></i>
                  </span>
                  <div>
                    <div className="font-medium">{s.title}</div>
                    <div className="mt-0.5 text-xs text-foreground-500">
                      {s.desc}
                    </div>
                  </div>
                </li>
              ))}
            </ol>
          </div>
        </div>
      </section>
    </div>
  );
}

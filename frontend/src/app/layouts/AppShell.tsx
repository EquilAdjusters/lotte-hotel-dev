import { NavLink, useLocation } from "react-router-dom";
import type { ReactNode } from "react";

interface AppShellProps {
  children: ReactNode;
}

const navItems = [
  { to: "/", label: "대시보드", icon: "ri-dashboard-2-line" },
  { to: "/claims/new", label: "사고 접수", icon: "ri-clipboard-line" },
  { to: "/claims", label: "현황 조회", icon: "ri-search-eye-line" },
  { to: "/admin/claims", label: "클레임 관리", icon: "ri-table-line" },
  { to: "/admin/accounts", label: "관리자 설정", icon: "ri-settings-3-line" },
];

export default function AppShell({ children }: AppShellProps) {
  const location = useLocation();

  const currentTitle =
    navItems.find((n) => n.to === location.pathname)?.label ?? "클레임 관리";

  return (
    <div className="min-h-screen w-full bg-background-50 text-foreground-950">
      <header className="sticky top-0 z-30 border-b border-background-200/70 bg-background-50/90 backdrop-blur">
        <div className="mx-auto flex h-16 w-full max-w-[1440px] items-center justify-between px-6">
          <div className="flex items-center gap-8">
            <a href="/" className="flex items-center gap-3 cursor-pointer whitespace-nowrap">
              <span className="w-9 h-9 flex items-center justify-center rounded-md bg-primary-500 text-background-50">
                <i className="ri-building-line text-lg"></i>
              </span>
              <div className="leading-tight">
                <div className="flex items-center gap-2">
                  <span className="font-heading text-base font-semibold">호텔롯데</span>
                  <span className="text-[10px] tracking-[0.14em] text-foreground-400">×</span>
                  <span className="font-heading text-sm font-medium text-foreground-600">WISE</span>
                </div>
                <div className="text-[11px] tracking-[0.14em] text-foreground-500">
                  클레임 관리 시스템
                </div>
              </div>
            </a>
            <nav className="hidden md:flex items-center gap-1">
              {navItems.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === "/"}
                  className={({ isActive }) =>
                    `flex items-center gap-2 rounded-md px-3 py-2 text-sm cursor-pointer whitespace-nowrap transition ${
                      isActive
                        ? "bg-primary-500 text-background-50"
                        : "text-foreground-700 hover:bg-background-100"
                    }`
                  }
                >
                  <span className="w-4 h-4 flex items-center justify-center">
                    <i className={item.icon}></i>
                  </span>
                  {item.label}
                </NavLink>
              ))}
            </nav>
          </div>
          <div className="flex items-center gap-3">
            <NavLink
              to="/login"
              className="rounded-md bg-primary-500 px-4 py-2 text-sm text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
            >
              로그인
            </NavLink>
          </div>
        </div>
        <nav className="md:hidden flex items-center gap-1 border-t border-background-200/70 px-4 py-2 overflow-x-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === "/"}
              className={({ isActive }) =>
                `flex items-center gap-1 rounded-md px-3 py-1.5 text-xs whitespace-nowrap ${
                  isActive
                    ? "bg-primary-500 text-background-50"
                    : "text-foreground-700 hover:bg-background-100"
                }`
              }
            >
              <i className={item.icon}></i>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="mx-auto w-full max-w-[1440px] px-6 py-10">
        <div className="mb-6 flex items-end justify-between">
          <div>
            <div className="text-xs tracking-[0.2em] text-foreground-500 uppercase">
              WISE × Hotel Lotte
            </div>
            <h1 className="mt-1 font-heading text-2xl md:text-3xl font-semibold">
              {currentTitle}
            </h1>
          </div>
          <div className="hidden md:flex items-center gap-2 text-sm text-foreground-500">
            <i className="ri-time-line"></i>
            <span>
              {new Date().toLocaleDateString("ko-KR", {
                year: "numeric",
                month: "long",
                day: "numeric",
                weekday: "long",
              })}
            </span>
          </div>
        </div>
        {children}
      </main>

      <footer className="border-t border-background-200/70 bg-background-100">
        <div className="mx-auto flex w-full max-w-[1440px] flex-col items-center justify-between gap-2 px-6 py-6 text-xs text-foreground-500 sm:flex-row">
          <span>
            © {new Date().getFullYear()} WISE Insurance Brokerage × Hotel Lotte · Claim Management System
          </span>
          <span>내부 직원 전용 시스템 · v1.0</span>
        </div>
      </footer>
    </div>
  );
}

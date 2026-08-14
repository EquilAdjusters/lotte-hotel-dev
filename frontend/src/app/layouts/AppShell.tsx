import { NavLink, useLocation, useNavigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "@/shared/hooks/useAuth";
import type { Role } from "@/entities/user/model/types";
import { logoutRequest } from "@/features/auth/api/authApi";

interface AppShellProps {
  children: ReactNode;
}

const navItems: { to: string; label: string; icon: string; roles?: Role[] }[] = [
  { to: "/dashboard", label: "대시보드", icon: "ri-dashboard-2-line" },
  { to: "/claims/new", label: "사고 접수", icon: "ri-clipboard-line" },
  { to: "/claims", label: "현황 조회", icon: "ri-search-eye-line" },
  {
    to: "/admin/claims",
    label: "클레임 관리",
    icon: "ri-table-line",
    roles: ["ADMIN1", "ADMIN2"],
  },
  {
    to: "/admin/accounts",
    label: "관리자 설정",
    icon: "ri-settings-3-line",
    roles: ["ADMIN1"],
  },
];

const roleLabels: Record<Role, string> = {
  ADMIN1: "와이즈 관리자",
  ADMIN2: "이퀼손사 센터장",
  ADMIN3: "호텔 본사 관리자",
  ADMIN4: "롯데 권역 영업지원팀",
  BRANCH_SHARED: "지점 공유계정",
};

export default function AppShell({ children }: AppShellProps) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    try {
      await logoutRequest();
    } catch {
      // 세션이 이미 만료된 경우 등은 무시하고 로컬 상태만 정리한다.
    }
    logout();
    navigate("/login", { replace: true });
  };

  const visibleNavItems = navItems.filter(
    (item) => !item.roles || (user && item.roles.includes(user.role))
  );

  const currentTitle =
    navItems.find((n) => n.to === location.pathname)?.label ?? "클레임 관리";

  return (
    <div className="min-h-screen w-full bg-background-50 text-foreground-950">
      <header className="sticky top-0 z-30 border-b border-background-200/70 bg-background-50/90 backdrop-blur">
        <div className="mx-auto flex h-16 w-full max-w-[1440px] items-center justify-between px-6">
          <div className="flex items-center gap-8">
            <a href="/dashboard" className="flex items-center gap-3 cursor-pointer whitespace-nowrap">
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
              {visibleNavItems.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end
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
            {user ? (
              <>
                <div className="hidden sm:flex flex-col items-end leading-tight">
                  <span className="text-sm font-medium">{user.displayName}</span>
                  <span className="text-[11px] text-foreground-500">
                    {roleLabels[user.role]}
                  </span>
                </div>
                <div className="w-9 h-9 flex items-center justify-center rounded-full bg-secondary-100 text-secondary-900 text-sm font-medium">
                  {user.displayName.slice(0, 1)}
                </div>
                <button
                  type="button"
                  onClick={handleLogout}
                  className="flex items-center gap-1 rounded-md border border-background-300/60 px-3 py-2 text-sm text-foreground-700 hover:bg-background-100 cursor-pointer whitespace-nowrap"
                >
                  <span className="w-4 h-4 flex items-center justify-center">
                    <i className="ri-logout-box-r-line"></i>
                  </span>
                  로그아웃
                </button>
              </>
            ) : (
              <NavLink
                to="/login"
                className="rounded-md bg-primary-500 px-4 py-2 text-sm text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
              >
                로그인
              </NavLink>
            )}
          </div>
        </div>
        <nav className="md:hidden flex items-center gap-1 border-t border-background-200/70 px-4 py-2 overflow-x-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end
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

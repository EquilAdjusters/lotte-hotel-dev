import { useState } from "react";
import { useNavigate } from "react-router-dom";

const demoAccounts = [
  { id: "manager", pw: "hotel2026", name: "김민준", role: "지배인 · 롯데호텔서울" },
  { id: "staff", pw: "hotel2026", name: "이서준", role: "프론트 데스크" },
  { id: "wise", pw: "hotel2026", name: "박지현", role: "WISE 담당 심사역" },
];

export default function LoginPage() {
  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");
  const [remember, setRemember] = useState(true);
  const [showPw, setShowPw] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showContactPopup, setShowContactPopup] = useState(false);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    if (!id.trim() || !pw.trim()) {
      setError("아이디 또는 비밀번호를 입력해 주세요.");
      return;
    }
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      navigate("/");
    }, 400);
  };

  const fillDemo = (account: (typeof demoAccounts)[number]) => {
    setId(account.id);
    setPw(account.pw);
    setError(null);
  };

  return (
    <div className="grid min-h-screen w-full grid-cols-1 lg:grid-cols-[1fr_0.95fr] bg-background-50">
      {/* Left Panel */}
      <div className="relative hidden lg:flex flex-col overflow-hidden bg-primary-900">
        <div className="absolute inset-0 bg-gradient-to-b from-primary-900/40 via-primary-900/30 to-primary-950/65"></div>

        <div className="relative z-10 flex items-center gap-6 px-12 pt-10">
          <div className="flex items-center gap-3">
            <span className="w-10 h-10 flex items-center justify-center rounded-md bg-background-50/15 backdrop-blur">
              <i className="ri-building-line text-xl text-background-50"></i>
            </span>
            <div className="leading-tight">
              <div className="font-heading text-lg font-semibold text-background-50">
                호텔롯데
              </div>
              <div className="text-[11px] tracking-[0.22em] uppercase text-background-50/70">
                Hotel Lotte
              </div>
            </div>
          </div>
          <div className="h-8 w-px bg-background-50/20"></div>
          <div className="leading-tight">
            <div className="text-xs tracking-[0.18em] uppercase text-background-50/60">
              Powered by
            </div>
            <div className="font-heading text-lg font-semibold text-background-50">
              WISE
            </div>
            <div className="text-[11px] tracking-[0.1em] text-background-50/70">
              보험중개
            </div>
          </div>
        </div>

        <div className="relative z-10 flex flex-1 flex-col justify-center px-12">
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-background-50/25 bg-background-50/10 px-4 py-1.5 backdrop-blur w-fit">
            <span className="w-2 h-2 flex items-center justify-center rounded-full bg-accent-400"></span>
            <span className="text-xs tracking-[0.2em] uppercase text-background-50/90">
              Claim Portal · Enterprise
            </span>
          </div>
          <h2 className="font-heading text-4xl leading-tight text-background-50">
            사고 발생부터
            <br />
            보상 완료까지,
            <br />
            <span className="text-accent-400">한 곳에서.</span>
          </h2>
        </div>

        <div className="relative z-10 border-t border-background-50/10 px-12 py-6">
          <div className="flex items-center gap-6 text-[11px] text-background-50/50">
            <span>WISE Insurance Brokerage Co., Ltd.</span>
            <span className="hidden sm:inline">|</span>
            <span className="hidden sm:inline">고객사 전용 내부 시스템</span>
            <span className="hidden sm:inline">|</span>
            <span>© 2026</span>
          </div>
        </div>
      </div>

      {/* Right Panel - Login Form */}
      <div className="flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md">
          <div className="lg:hidden mb-8 flex items-center gap-3">
            <div className="flex items-center gap-3">
              <span className="w-10 h-10 flex items-center justify-center rounded-md bg-primary-500 text-background-50">
                <i className="ri-building-line text-lg"></i>
              </span>
              <div className="leading-tight">
                <div className="font-heading text-base font-semibold">
                  호텔롯데
                </div>
                <div className="text-[11px] tracking-[0.16em] text-foreground-500">
                  WISE 클레임 포털
                </div>
              </div>
            </div>
          </div>

          <div className="text-xs tracking-[0.2em] uppercase text-foreground-500">
            WISE × Hotel Lotte
          </div>
          <h1 className="mt-2 font-heading text-3xl font-semibold text-foreground-950">
            클레임 포털 로그인
          </h1>
          <p className="mt-2 text-sm leading-relaxed text-foreground-600">
            호텔롯데 직원 및 WISE 담당자 전용 시스템입니다.
            발급받은 계정으로 로그인해 주세요.
          </p>

          <form onSubmit={handleSubmit} className="mt-8 space-y-4">
            <div>
              <label
                htmlFor="login-id"
                className="mb-1.5 block text-xs font-medium text-foreground-700"
              >
                아이디
              </label>
              <div className="flex items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3 focus-within:border-primary-400">
                <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
                  <i className="ri-user-line"></i>
                </span>
                <input
                  id="login-id"
                  name="username"
                  type="text"
                  autoComplete="username"
                  value={id}
                  onChange={(e) => setId(e.target.value)}
                  placeholder="예: staff"
                  className="w-full bg-transparent py-3 text-sm outline-none placeholder:text-foreground-400"
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="login-pw"
                className="mb-1.5 block text-xs font-medium text-foreground-700"
              >
                비밀번호
              </label>
              <div className="flex items-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-3 focus-within:border-primary-400">
                <span className="w-5 h-5 flex items-center justify-center text-foreground-500">
                  <i className="ri-lock-2-line"></i>
                </span>
                <input
                  id="login-pw"
                  name="password"
                  type={showPw ? "text" : "password"}
                  autoComplete="current-password"
                  value={pw}
                  onChange={(e) => setPw(e.target.value)}
                  placeholder="비밀번호"
                  className="w-full bg-transparent py-3 text-sm outline-none placeholder:text-foreground-400"
                />
                <button
                  type="button"
                  onClick={() => setShowPw((v) => !v)}
                  className="w-6 h-6 flex items-center justify-center text-foreground-500 hover:text-foreground-800 cursor-pointer"
                  aria-label="비밀번호 표시 전환"
                >
                  <i className={showPw ? "ri-eye-off-line" : "ri-eye-line"}></i>
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 text-sm text-foreground-700 cursor-pointer">
                <input
                  type="checkbox"
                  checked={remember}
                  onChange={(e) => setRemember(e.target.checked)}
                  className="h-4 w-4 rounded border-background-300 accent-primary-500 cursor-pointer"
                />
                로그인 유지
              </label>
              <button
                type="button"
                className="text-sm text-foreground-600 hover:text-primary-600 cursor-pointer whitespace-nowrap"
              >
                비밀번호 찾기
              </button>
            </div>

            {error && (
              <div className="flex items-center gap-2 rounded-md border border-accent-300 bg-accent-100/70 px-3 py-2 text-sm text-accent-900">
                <i className="ri-error-warning-line"></i>
                <span>{error}</span>
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-md bg-primary-500 py-3 text-sm font-medium text-background-50 hover:bg-primary-600 disabled:opacity-70 cursor-pointer whitespace-nowrap"
            >
              {loading ? "로그인 중..." : "로그인"}
            </button>
          </form>

          <div className="mt-5 flex flex-col gap-2">
            <button
              type="button"
              onClick={() => setShowContactPopup(true)}
              className="flex items-center justify-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-4 py-2.5 text-sm text-foreground-700 hover:border-secondary-300 hover:bg-secondary-50 cursor-pointer whitespace-nowrap"
            >
              <span className="w-4 h-4 flex items-center justify-center">
                <i className="ri-customer-service-line"></i>
              </span>
              관리자 문의하기
            </button>
          </div>

          <div className="mt-8 rounded-md border border-background-200/70 bg-background-100 p-4">
            <div className="flex items-center gap-2 text-xs font-medium text-foreground-700">
              <span className="w-4 h-4 flex items-center justify-center text-accent-700">
                <i className="ri-flashlight-line"></i>
              </span>
              데모 계정 (클릭 시 자동 입력)
            </div>
            <div className="mt-3 grid grid-cols-1 gap-2 sm:grid-cols-3">
              {demoAccounts.map((a) => (
                <button
                  key={a.id}
                  type="button"
                  onClick={() => fillDemo(a)}
                  className="rounded-md border border-background-200 bg-background-50 px-3 py-2 text-left hover:border-primary-300 hover:bg-primary-50 cursor-pointer"
                >
                  <div className="text-sm font-medium">{a.name}</div>
                  <div className="text-[11px] text-foreground-500">{a.role}</div>
                  <div className="mt-1 font-mono text-[11px] text-foreground-600">
                    {a.id} / {a.pw}
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div className="mt-6 rounded-md border border-background-200/70 bg-background-50 p-4">
            <div className="flex items-start gap-3">
              <span className="w-8 h-8 flex items-center justify-center rounded-md bg-secondary-100 text-secondary-900 text-xs">
                <i className="ri-shield-check-line"></i>
              </span>
              <div>
                <div className="text-xs font-medium text-foreground-700">
                  WISE 보안 정책
                </div>
                <p className="mt-1 text-[11px] leading-relaxed text-foreground-500">
                  본 시스템은 TLS 1.3 암호화 통신을 사용하며, 모든 접속 기록은
                  금융보안규정에 따라 5년간 보관됩니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Admin Contact Popup */}
      {showContactPopup && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-foreground-950/50 backdrop-blur-sm"
          onClick={() => setShowContactPopup(false)}
        >
          <div
            className="mx-4 w-full max-w-sm rounded-lg bg-background-50 p-6"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <span className="w-10 h-10 flex items-center justify-center rounded-full bg-secondary-100">
                  <i className="ri-customer-service-line text-lg text-secondary-700"></i>
                </span>
                <div>
                  <h3 className="font-heading text-base font-semibold text-foreground-950">
                    관리자 문의
                  </h3>
                  <p className="text-xs text-foreground-500">
                    WISE 보험중개 클레임 관리팀
                  </p>
                </div>
              </div>
              <button
                onClick={() => setShowContactPopup(false)}
                className="w-8 h-8 flex items-center justify-center rounded-md text-foreground-500 hover:bg-background-100 cursor-pointer"
                aria-label="닫기"
              >
                <i className="ri-close-line"></i>
              </button>
            </div>

            <div className="mt-5 rounded-md border border-background-200 bg-background-100 p-4">
              <div className="text-center">
                <div className="text-xs text-foreground-500">대표 번호</div>
                <a
                  href="tel:02-3456-7890"
                  className="mt-1 block font-heading text-2xl font-semibold text-foreground-950 hover:text-primary-600 cursor-pointer"
                >
                  02-3456-7890
                </a>
                <div className="mt-3 text-[11px] text-foreground-500">
                  평일 09:00 - 18:00 / 주말·공휴일 휴무
                </div>
              </div>
            </div>

            <div className="mt-4 text-center">
              <button
                onClick={() => setShowContactPopup(false)}
                className="rounded-md bg-primary-500 px-6 py-2 text-sm font-medium text-background-50 hover:bg-primary-600 cursor-pointer whitespace-nowrap"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

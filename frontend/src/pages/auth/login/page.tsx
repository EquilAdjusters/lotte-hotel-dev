import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "@/shared/hooks/useAuth";
import { loginRequest } from "@/features/auth/api/authApi";

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");
  const [remember, setRemember] = useState(true);
  const [showPw, setShowPw] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showContactPopup, setShowContactPopup] = useState(false);

  useEffect(() => {
    if (user) navigate("/dashboard", { replace: true });
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    if (!id.trim() || !pw.trim()) {
      setError("아이디 또는 비밀번호를 입력해 주세요.");
      return;
    }
    setLoading(true);
    try {
      const authenticatedUser = await loginRequest({
        loginId: id.trim(),
        password: pw,
      });
      login(authenticatedUser);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const message = (err.response?.data as { message?: string } | undefined)
          ?.message;
        setError(message ?? "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
      } else {
        setError("서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen w-full flex-col bg-background-50">
      {/* Top Brand Header */}
      <header className="relative border-b border-background-200/70 bg-background-50">
        <div className="mx-auto flex w-full max-w-4xl flex-col items-center px-6 py-10 text-center md:py-14">
          <div className="flex items-center gap-3">
            <span className="w-11 h-11 flex items-center justify-center rounded-lg bg-primary-500 text-background-50">
              <i className="ri-building-line text-xl"></i>
            </span>
            <div className="leading-tight text-left">
              <div className="font-heading text-lg font-semibold text-foreground-950">
                호텔롯데
              </div>
              <div className="text-[11px] tracking-[0.18em] text-foreground-500">
                WISE 클레임 포털
              </div>
            </div>
          </div>

          <p className="mt-6 font-heading text-lg leading-relaxed text-foreground-950 md:text-2xl">
            사고 발생부터 보상 완료까지,{" "}
            <span className="text-accent-700">한 곳에서.</span>
          </p>
        </div>
      </header>

      {/* Login Form */}
      <main className="flex flex-1 items-center justify-center px-6 py-10 md:py-12">
        <div className="w-full max-w-md">
          <div className="text-center">
            <div className="text-xs tracking-[0.2em] uppercase text-foreground-500">
              WISE × Hotel Lotte
            </div>
            <h1 className="mt-2 font-heading text-2xl font-semibold text-foreground-950 md:text-3xl">
              클레임 포털 로그인
            </h1>
            <p className="mt-2 text-sm leading-relaxed text-foreground-600">
              호텔롯데 임직원 및 WISE 담당자 전용 시스템입니다.
              <br className="hidden sm:block" />
              발급받은 계정으로 로그인해 주세요.
            </p>
          </div>

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
                  placeholder="아이디를 입력하세요"
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
                  placeholder="비밀번호를 입력하세요"
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

            <label className="flex items-center gap-2 text-sm text-foreground-700 cursor-pointer">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
                className="h-4 w-4 rounded border-background-300 accent-primary-500 cursor-pointer"
              />
              로그인 유지
            </label>

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

          {/* Action buttons below login */}
          <div className="mt-5 flex flex-col gap-2">
            <a
              href="#"
              className="flex items-center justify-center gap-2 rounded-md border border-background-300/60 bg-background-50 px-4 py-2.5 text-sm text-foreground-700 hover:border-primary-300 hover:bg-primary-50 cursor-pointer whitespace-nowrap"
              onClick={(e) => {
                e.preventDefault();
                // TODO: 실제 동의서 파일 다운로드 링크로 교체
              }}
            >
              <span className="w-4 h-4 flex items-center justify-center">
                <i className="ri-file-download-line"></i>
              </span>
              제3자 정보제공활용동의서 다운로드
            </a>
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
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-background-200/70 px-6 py-5">
        <div className="mx-auto flex w-full max-w-4xl flex-col items-center gap-1 text-center text-[11px] text-foreground-500">
          <span>
            WISE Insurance Brokerage Co., Ltd. · 고객사 전용 내부 시스템
          </span>
          <span>© 2026 WISE 보험중개</span>
        </div>
      </footer>

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

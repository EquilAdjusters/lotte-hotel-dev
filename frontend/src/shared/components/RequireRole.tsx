import type { ReactNode } from "react";
import { useAuth } from "@/shared/hooks/useAuth";
import type { Role } from "@/entities/user/model/types";

export default function RequireRole({
  roles,
  children,
}: {
  roles: Role[];
  children: ReactNode;
}) {
  const { user } = useAuth();

  if (!user || !roles.includes(user.role)) {
    return (
      <div className="flex min-h-[400px] flex-col items-center justify-center gap-3 rounded-lg border border-background-200/70 bg-background-50 p-10 text-center">
        <span className="w-12 h-12 flex items-center justify-center rounded-full bg-background-100 text-foreground-500">
          <i className="ri-lock-2-line text-xl"></i>
        </span>
        <div className="text-sm text-foreground-600">
          이 페이지에 접근할 수 있는 권한이 없습니다.
        </div>
      </div>
    );
  }

  return <>{children}</>;
}

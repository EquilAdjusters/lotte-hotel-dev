import { useEffect, useState } from "react";
import type { AuthenticatedUser } from "@/entities/user/model/types";

const AUTH_KEY = "lotte_claim_auth_user";
const AUTH_CHANGE_EVENT = "lotte-claim-auth-change";

export function getStoredUser(): AuthenticatedUser | null {
  try {
    const raw = localStorage.getItem(AUTH_KEY);
    if (!raw) return null;
    return JSON.parse(raw) as AuthenticatedUser;
  } catch {
    return null;
  }
}

export function useAuth() {
  const [user, setUser] = useState<AuthenticatedUser | null>(() =>
    getStoredUser()
  );

  useEffect(() => {
    const handler = () => setUser(getStoredUser());
    window.addEventListener("storage", handler);
    window.addEventListener(AUTH_CHANGE_EVENT, handler);
    return () => {
      window.removeEventListener("storage", handler);
      window.removeEventListener(AUTH_CHANGE_EVENT, handler);
    };
  }, []);

  const login = (u: AuthenticatedUser) => {
    localStorage.setItem(AUTH_KEY, JSON.stringify(u));
    window.dispatchEvent(new Event(AUTH_CHANGE_EVENT));
    setUser(u);
  };

  const logout = () => {
    localStorage.removeItem(AUTH_KEY);
    window.dispatchEvent(new Event(AUTH_CHANGE_EVENT));
    setUser(null);
  };

  return { user, login, logout };
}

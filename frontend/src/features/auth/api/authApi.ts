import { apiClient } from "@/shared/api/client";
import type { AuthenticatedUser } from "@/entities/user/model/types";

export interface LoginPayload {
  loginId: string;
  password: string;
}

export async function loginRequest(
  payload: LoginPayload
): Promise<AuthenticatedUser> {
  const { data } = await apiClient.post<AuthenticatedUser>(
    "/api/auth/login",
    payload
  );
  return data;
}

export async function logoutRequest(): Promise<void> {
  await apiClient.post("/api/auth/logout");
}

export async function fetchCurrentUser(): Promise<AuthenticatedUser> {
  const { data } = await apiClient.get<AuthenticatedUser>("/api/auth/me");
  return data;
}

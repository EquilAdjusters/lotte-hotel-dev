import { apiClient } from "@/shared/api/client";
import type { SpringPage } from "@/entities/claim/model/types";
import type {
  AccountCreatePayload,
  AccountHistoryResponse,
  AccountResponse,
  AccountSearchCondition,
  AccountUpdatePayload,
} from "@/entities/account/model/types";

function withoutEmpty<T extends object>(params: T): Partial<T> {
  const entries = Object.entries(params).filter(
    ([, value]) => value !== null && value !== undefined && value !== ""
  );
  return Object.fromEntries(entries) as Partial<T>;
}

export interface FetchAccountsParams extends AccountSearchCondition {
  page?: number;
  size?: number;
}

export async function fetchAccounts(
  params: FetchAccountsParams
): Promise<SpringPage<AccountResponse>> {
  const { data } = await apiClient.get<SpringPage<AccountResponse>>(
    "/api/accounts",
    { params: withoutEmpty(params) }
  );
  return data;
}

export async function createAccount(
  payload: AccountCreatePayload
): Promise<AccountResponse> {
  const { data } = await apiClient.post<AccountResponse>("/api/accounts", payload);
  return data;
}

export async function updateAccount(
  accountId: number,
  payload: AccountUpdatePayload
): Promise<AccountResponse> {
  const { data } = await apiClient.patch<AccountResponse>(
    `/api/accounts/${accountId}`,
    payload
  );
  return data;
}

export async function resetAccountPassword(
  accountId: number,
  newPassword: string,
  newPasswordConfirm: string
): Promise<void> {
  await apiClient.patch(`/api/accounts/${accountId}/password-reset`, {
    newPassword,
    newPasswordConfirm,
  });
}

export async function unlockAccount(accountId: number): Promise<AccountResponse> {
  const { data } = await apiClient.patch<AccountResponse>(
    `/api/accounts/${accountId}/unlock`
  );
  return data;
}

export async function deactivateAccount(accountId: number): Promise<AccountResponse> {
  const { data } = await apiClient.patch<AccountResponse>(
    `/api/accounts/${accountId}/deactivate`
  );
  return data;
}

export async function activateAccount(accountId: number): Promise<AccountResponse> {
  const { data } = await apiClient.patch<AccountResponse>(
    `/api/accounts/${accountId}/activate`
  );
  return data;
}

export async function deleteAccount(accountId: number): Promise<void> {
  await apiClient.delete(`/api/accounts/${accountId}`);
}

export async function fetchAccountHistories(
  accountId: number,
  params: { page?: number; size?: number } = {}
): Promise<SpringPage<AccountHistoryResponse>> {
  const { data } = await apiClient.get<SpringPage<AccountHistoryResponse>>(
    `/api/accounts/${accountId}/histories`,
    { params: withoutEmpty(params) }
  );
  return data;
}

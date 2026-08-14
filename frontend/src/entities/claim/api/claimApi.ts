import { apiClient } from "@/shared/api/client";
import type {
  ClaimCreatePayload,
  ClaimDuplicateResponse,
  ClaimHistoryResponse,
  ClaimListResponse,
  ClaimResponse,
  ClaimSearchCondition,
  ClaimUpdatePayload,
  SpringPage,
} from "@/entities/claim/model/types";

function withoutEmpty<T extends object>(params: T): Partial<T> {
  const entries = Object.entries(params).filter(
    ([, value]) => value !== null && value !== undefined && value !== ""
  );
  return Object.fromEntries(entries) as Partial<T>;
}

export async function createClaim(
  payload: ClaimCreatePayload
): Promise<ClaimResponse> {
  const { data } = await apiClient.post<ClaimResponse>("/api/claims", payload);
  return data;
}

export interface FetchClaimsParams extends ClaimSearchCondition {
  page?: number;
  size?: number;
  sort?: string;
}

export async function fetchClaims(
  params: FetchClaimsParams
): Promise<SpringPage<ClaimListResponse>> {
  const { data } = await apiClient.get<SpringPage<ClaimListResponse>>(
    "/api/claims",
    { params: withoutEmpty(params) }
  );
  return data;
}

export async function fetchClaim(claimId: number): Promise<ClaimResponse> {
  const { data } = await apiClient.get<ClaimResponse>(`/api/claims/${claimId}`);
  return data;
}

export async function updateClaim(
  claimId: number,
  payload: ClaimUpdatePayload
): Promise<ClaimResponse> {
  const { data } = await apiClient.patch<ClaimResponse>(
    `/api/claims/${claimId}`,
    payload
  );
  return data;
}

export async function cancelClaim(claimId: number): Promise<void> {
  await apiClient.post(`/api/claims/${claimId}/cancel`);
}

export async function fetchClaimHistories(
  claimId: number,
  params: { page?: number; size?: number } = {}
): Promise<SpringPage<ClaimHistoryResponse>> {
  const { data } = await apiClient.get<SpringPage<ClaimHistoryResponse>>(
    `/api/claims/${claimId}/histories`,
    { params: withoutEmpty(params) }
  );
  return data;
}

export async function fetchDuplicateClaims(
  victimName: string,
  victimBirthDate: string
): Promise<ClaimDuplicateResponse[]> {
  const { data } = await apiClient.get<ClaimDuplicateResponse[]>(
    "/api/claims/duplicates",
    { params: { victimName, victimBirthDate } }
  );
  return data;
}

export async function exportClaims(
  condition: ClaimSearchCondition
): Promise<Blob> {
  const { data } = await apiClient.get<Blob>("/api/claims/export", {
    params: withoutEmpty(condition),
    responseType: "blob",
  });
  return data;
}

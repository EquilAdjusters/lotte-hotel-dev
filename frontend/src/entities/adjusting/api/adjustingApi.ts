import { apiClient } from "@/shared/api/client";
import type { SpringPage } from "@/entities/claim/model/types";
import type {
  AdjustingCompanyOption,
  AdjustingCompanyPayload,
  AdjusterCreatePayload,
  AdjusterOption,
  AdjusterUpdatePayload,
  ClaimAssignmentListResponse,
  ClaimAssignmentSearchCondition,
} from "@/entities/adjusting/model/types";

function withoutEmpty<T extends object>(params: T): Partial<T> {
  const entries = Object.entries(params).filter(
    ([, value]) => value !== null && value !== undefined && value !== ""
  );
  return Object.fromEntries(entries) as Partial<T>;
}

export interface FetchAssignmentClaimsParams extends ClaimAssignmentSearchCondition {
  page?: number;
  size?: number;
}

export async function fetchAssignmentClaims(
  params: FetchAssignmentClaimsParams
): Promise<SpringPage<ClaimAssignmentListResponse>> {
  const { data } = await apiClient.get<SpringPage<ClaimAssignmentListResponse>>(
    "/api/adjusting/claims",
    { params: withoutEmpty(params) }
  );
  return data;
}

export async function fetchAdjustingCompanies(
  activeOnly = true
): Promise<AdjustingCompanyOption[]> {
  const { data } = await apiClient.get<AdjustingCompanyOption[]>(
    "/api/adjusting/companies",
    { params: { activeOnly } }
  );
  return data;
}

export async function fetchAvailableCompaniesForClaim(
  claimId: number
): Promise<AdjustingCompanyOption[]> {
  const { data } = await apiClient.get<AdjustingCompanyOption[]>(
    `/api/adjusting/claims/${claimId}/available-companies`
  );
  return data;
}

export async function fetchAdjusters(
  companyId: number,
  activeOnly = true
): Promise<AdjusterOption[]> {
  const { data } = await apiClient.get<AdjusterOption[]>(
    `/api/adjusting/companies/${companyId}/adjusters`,
    { params: { activeOnly } }
  );
  return data;
}

export async function assignClaim(
  claimId: number,
  payload: { adjustingCompanyId: number; adjusterId: number }
): Promise<void> {
  await apiClient.post(`/api/adjusting/claims/${claimId}/assign`, payload);
}

export async function createAdjustingCompany(
  payload: AdjustingCompanyPayload
): Promise<AdjustingCompanyOption> {
  const { data } = await apiClient.post<AdjustingCompanyOption>(
    "/api/adjusting/companies",
    payload
  );
  return data;
}

export async function updateAdjustingCompany(
  companyId: number,
  payload: AdjustingCompanyPayload
): Promise<AdjustingCompanyOption> {
  const { data } = await apiClient.patch<AdjustingCompanyOption>(
    `/api/adjusting/companies/${companyId}`,
    payload
  );
  return data;
}

export async function deactivateAdjustingCompany(companyId: number): Promise<void> {
  await apiClient.post(`/api/adjusting/companies/${companyId}/deactivate`);
}

export async function activateAdjustingCompany(companyId: number): Promise<void> {
  await apiClient.post(`/api/adjusting/companies/${companyId}/activate`);
}

export async function createAdjuster(
  payload: AdjusterCreatePayload
): Promise<AdjusterOption> {
  const { data } = await apiClient.post<AdjusterOption>(
    "/api/adjusting/adjusters",
    payload
  );
  return data;
}

export async function updateAdjuster(
  adjusterId: number,
  payload: AdjusterUpdatePayload
): Promise<AdjusterOption> {
  const { data } = await apiClient.patch<AdjusterOption>(
    `/api/adjusting/adjusters/${adjusterId}`,
    payload
  );
  return data;
}

export async function deactivateAdjuster(adjusterId: number): Promise<void> {
  await apiClient.post(`/api/adjusting/adjusters/${adjusterId}/deactivate`);
}

export async function activateAdjuster(adjusterId: number): Promise<void> {
  await apiClient.post(`/api/adjusting/adjusters/${adjusterId}/activate`);
}

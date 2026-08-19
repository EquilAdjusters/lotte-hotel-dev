import { apiClient } from "@/shared/api/client";
import type { BranchOption } from "@/entities/branch/model/types";

export async function fetchBranches(
  hotelId?: number,
  activeOnly = true
): Promise<BranchOption[]> {
  const { data } = await apiClient.get<BranchOption[]>("/api/branches", {
    params: { ...(hotelId ? { hotelId } : {}), activeOnly },
  });
  return data;
}

export async function createBranch(
  hotelId: number,
  name: string
): Promise<BranchOption> {
  const { data } = await apiClient.post<BranchOption>("/api/branches", {
    hotelId,
    name,
  });
  return data;
}

export async function updateBranch(
  branchId: number,
  hotelId: number,
  name: string
): Promise<BranchOption> {
  const { data } = await apiClient.patch<BranchOption>(`/api/branches/${branchId}`, {
    hotelId,
    name,
  });
  return data;
}

export async function deactivateBranch(branchId: number): Promise<void> {
  await apiClient.delete(`/api/branches/${branchId}`);
}

export async function activateBranch(branchId: number): Promise<BranchOption> {
  const { data } = await apiClient.patch<BranchOption>(
    `/api/branches/${branchId}/activate`
  );
  return data;
}

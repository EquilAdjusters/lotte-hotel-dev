import { apiClient } from "@/shared/api/client";
import type { BranchOption } from "@/entities/branch/model/types";

export async function fetchBranches(hotelId?: number): Promise<BranchOption[]> {
  const { data } = await apiClient.get<BranchOption[]>("/api/branches", {
    params: hotelId ? { hotelId } : {},
  });
  return data;
}

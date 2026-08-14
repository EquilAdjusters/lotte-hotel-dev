import { apiClient } from "@/shared/api/client";
import type { BranchOption } from "@/entities/branch/model/types";

export async function fetchBranches(): Promise<BranchOption[]> {
  const { data } = await apiClient.get<BranchOption[]>("/api/branches");
  return data;
}

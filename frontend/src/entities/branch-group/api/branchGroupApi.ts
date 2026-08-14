import { apiClient } from "@/shared/api/client";
import type { BranchGroupOption } from "@/entities/branch-group/model/types";

export async function fetchBranchGroups(): Promise<BranchGroupOption[]> {
  const { data } = await apiClient.get<BranchGroupOption[]>("/api/branch-groups");
  return data;
}

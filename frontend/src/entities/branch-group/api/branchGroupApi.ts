import { apiClient } from "@/shared/api/client";
import type {
  BranchGroupMember,
  BranchGroupOption,
} from "@/entities/branch-group/model/types";

export async function fetchBranchGroups(
  activeOnly = true
): Promise<BranchGroupOption[]> {
  const { data } = await apiClient.get<BranchGroupOption[]>("/api/branch-groups", {
    params: { activeOnly },
  });
  return data;
}

export async function createBranchGroup(name: string): Promise<BranchGroupOption> {
  const { data } = await apiClient.post<BranchGroupOption>("/api/branch-groups", {
    name,
  });
  return data;
}

export async function updateBranchGroup(
  branchGroupId: number,
  name: string
): Promise<BranchGroupOption> {
  const { data } = await apiClient.patch<BranchGroupOption>(
    `/api/branch-groups/${branchGroupId}`,
    { name }
  );
  return data;
}

export async function deactivateBranchGroup(branchGroupId: number): Promise<void> {
  await apiClient.delete(`/api/branch-groups/${branchGroupId}`);
}

export async function activateBranchGroup(
  branchGroupId: number
): Promise<BranchGroupOption> {
  const { data } = await apiClient.patch<BranchGroupOption>(
    `/api/branch-groups/${branchGroupId}/activate`
  );
  return data;
}

export async function fetchBranchGroupMembers(
  branchGroupId: number
): Promise<BranchGroupMember[]> {
  const { data } = await apiClient.get<BranchGroupMember[]>(
    `/api/branch-group-members/branch-groups/${branchGroupId}`
  );
  return data;
}

export async function addBranchGroupMember(
  branchGroupId: number,
  branchId: number
): Promise<BranchGroupMember> {
  const { data } = await apiClient.post<BranchGroupMember>(
    "/api/branch-group-members",
    { branchGroupId, branchId }
  );
  return data;
}

export async function removeBranchGroupMember(
  branchGroupId: number,
  branchId: number
): Promise<void> {
  await apiClient.delete(
    `/api/branch-group-members/branch-groups/${branchGroupId}/branches/${branchId}`
  );
}

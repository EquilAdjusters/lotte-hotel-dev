export interface BranchGroupOption {
  id: number;
  name: string;
  active: boolean;
}

export interface BranchGroupMember {
  id: number;
  branchGroupId: number;
  branchGroupName: string;
  branchId: number;
  branchName: string;
}

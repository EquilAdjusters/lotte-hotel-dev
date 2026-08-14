import type { Role, ScopeType } from "@/entities/user/model/types";

export type AccountStatus = "ACTIVE" | "INACTIVE" | "LOCKED" | "DELETED";

export interface AccountResponse {
  id: number;
  loginId: string;
  displayName: string;
  role: Role;
  scopeType: ScopeType;
  status: AccountStatus;
  sharedAccount: boolean;

  hotelCompanyId: number | null;
  hotelCompanyName: string | null;

  hotelId: number | null;
  hotelName: string | null;

  branchId: number | null;
  branchName: string | null;

  branchGroupId: number | null;
  branchGroupName: string | null;
}

export interface AccountCreatePayload {
  loginId: string;
  password: string;
  displayName: string;
  role: Role;
  scopeType: ScopeType;
  sharedAccount: boolean;
  hotelCompanyId: number | null;
  hotelId: number | null;
  branchId: number | null;
  branchGroupId: number | null;
}

export interface AccountUpdatePayload {
  displayName: string;
  role: Role;
  scopeType: ScopeType;
  sharedAccount: boolean;
  hotelCompanyId: number | null;
  hotelId: number | null;
  branchId: number | null;
  branchGroupId: number | null;
}

export interface AccountSearchCondition {
  keyword?: string | null;
  role?: Role | null;
  status?: AccountStatus | null;
}

export type Role = "ADMIN1" | "ADMIN2" | "ADMIN3" | "ADMIN4" | "BRANCH_SHARED";

export type ScopeType = "ALL" | "ASSIGNED" | "HOTEL" | "BRANCH" | "BRANCH_GROUP";

export interface AuthenticatedUser {
  accountId: number;
  loginId: string;
  displayName: string;
  role: Role;
  scopeType: ScopeType;
  hotelCompanyName: string | null;
  hotelName: string | null;
  branchName: string | null;
}

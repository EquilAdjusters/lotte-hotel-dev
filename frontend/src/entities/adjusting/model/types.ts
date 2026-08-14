export interface AdjustingCompanyOption {
  id: number;
  name: string;
  businessNumber: string;
  active: boolean;
}

export interface AdjustingCompanyPayload {
  name: string;
  businessNumber: string | null;
}

export interface AdjusterOption {
  id: number;
  adjustingCompanyId: number;
  name: string;
  phone: string;
  active: boolean;
}

export interface AdjusterCreatePayload {
  adjustingCompanyId: number;
  name: string;
  phone: string;
}

export interface AdjusterUpdatePayload {
  name: string;
  phone: string;
}

export interface ClaimAssignmentListResponse {
  claimId: number;
  claimNumber: string;
  receivedAt: string;
  accidentAt: string;
  hotelName: string;
  branchName: string;
  victimName: string;
  receivedByName: string;
  adjustingCompanyName: string | null;
  adjusterName: string | null;
  adjusterPhone: string | null;
  assignedAt: string | null;
  assigned: boolean;
  insuranceCompanyName: string | null;
  receiptEmail: string | null;
}

export interface ClaimAssignmentSearchCondition {
  receivedFrom?: string | null;
  receivedTo?: string | null;
  victimName?: string | null;
  assigned?: boolean | null;
}

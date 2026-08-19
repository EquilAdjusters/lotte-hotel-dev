export interface InsuranceCompanyOption {
  id: number;
  name: string;
  active: boolean;
}

export interface BranchInsuranceSetting {
  branchId: number;
  branchName: string;
  insuranceCompanyId: number | null;
  insuranceCompanyName: string | null;
  receiptEmail: string | null;
}

import { apiClient } from "@/shared/api/client";
import type {
  BranchInsuranceSetting,
  InsuranceCompanyOption,
} from "@/entities/insurance-company/model/types";

export async function fetchInsuranceCompanies(
  activeOnly = true
): Promise<InsuranceCompanyOption[]> {
  const { data } = await apiClient.get<InsuranceCompanyOption[]>(
    "/api/insurance-companies",
    { params: { activeOnly } }
  );
  return data;
}

export async function createInsuranceCompany(
  name: string
): Promise<InsuranceCompanyOption> {
  const { data } = await apiClient.post<InsuranceCompanyOption>(
    "/api/insurance-companies",
    { name }
  );
  return data;
}

export async function updateInsuranceCompany(
  insuranceCompanyId: number,
  name: string
): Promise<InsuranceCompanyOption> {
  const { data } = await apiClient.patch<InsuranceCompanyOption>(
    `/api/insurance-companies/${insuranceCompanyId}`,
    { name }
  );
  return data;
}

export async function deactivateInsuranceCompany(
  insuranceCompanyId: number
): Promise<void> {
  await apiClient.post(`/api/insurance-companies/${insuranceCompanyId}/deactivate`);
}

export async function activateInsuranceCompany(
  insuranceCompanyId: number
): Promise<void> {
  await apiClient.post(`/api/insurance-companies/${insuranceCompanyId}/activate`);
}

export async function fetchBranchInsuranceSetting(
  branchId: number
): Promise<BranchInsuranceSetting> {
  const { data } = await apiClient.get<BranchInsuranceSetting>(
    `/api/insurance-companies/branches/${branchId}`
  );
  return data;
}

export async function updateBranchInsuranceSetting(
  branchId: number,
  insuranceCompanyId: number,
  receiptEmail: string
): Promise<BranchInsuranceSetting> {
  const { data } = await apiClient.patch<BranchInsuranceSetting>(
    `/api/insurance-companies/branches/${branchId}`,
    { insuranceCompanyId, receiptEmail }
  );
  return data;
}

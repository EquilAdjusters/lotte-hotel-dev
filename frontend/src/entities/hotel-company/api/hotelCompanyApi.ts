import { apiClient } from "@/shared/api/client";
import type { HotelCompanyOption } from "@/entities/hotel-company/model/types";

export async function fetchHotelCompanies(
  activeOnly = true
): Promise<HotelCompanyOption[]> {
  const { data } = await apiClient.get<HotelCompanyOption[]>("/api/hotel-companies", {
    params: { activeOnly },
  });
  return data;
}

export async function createHotelCompany(name: string): Promise<HotelCompanyOption> {
  const { data } = await apiClient.post<HotelCompanyOption>("/api/hotel-companies", {
    name,
  });
  return data;
}

export async function updateHotelCompany(
  hotelCompanyId: number,
  name: string
): Promise<HotelCompanyOption> {
  const { data } = await apiClient.patch<HotelCompanyOption>(
    `/api/hotel-companies/${hotelCompanyId}`,
    { name }
  );
  return data;
}

export async function deactivateHotelCompany(hotelCompanyId: number): Promise<void> {
  await apiClient.delete(`/api/hotel-companies/${hotelCompanyId}`);
}

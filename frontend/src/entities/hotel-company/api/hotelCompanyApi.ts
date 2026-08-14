import { apiClient } from "@/shared/api/client";
import type { HotelCompanyOption } from "@/entities/hotel-company/model/types";

export async function fetchHotelCompanies(): Promise<HotelCompanyOption[]> {
  const { data } = await apiClient.get<HotelCompanyOption[]>("/api/hotel-companies");
  return data;
}

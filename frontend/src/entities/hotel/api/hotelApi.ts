import { apiClient } from "@/shared/api/client";
import type { HotelOption } from "@/entities/hotel/model/types";

export async function fetchHotels(hotelCompanyId?: number): Promise<HotelOption[]> {
  const { data } = await apiClient.get<HotelOption[]>("/api/hotels", {
    params: hotelCompanyId ? { hotelCompanyId } : {},
  });
  return data;
}

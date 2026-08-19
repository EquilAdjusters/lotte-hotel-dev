import { apiClient } from "@/shared/api/client";
import type { HotelOption } from "@/entities/hotel/model/types";

export async function fetchHotels(
  hotelCompanyId?: number,
  activeOnly = true
): Promise<HotelOption[]> {
  const { data } = await apiClient.get<HotelOption[]>("/api/hotels", {
    params: { ...(hotelCompanyId ? { hotelCompanyId } : {}), activeOnly },
  });
  return data;
}

export async function createHotel(
  hotelCompanyId: number,
  name: string
): Promise<HotelOption> {
  const { data } = await apiClient.post<HotelOption>("/api/hotels", {
    hotelCompanyId,
    name,
  });
  return data;
}

export async function updateHotel(
  hotelId: number,
  hotelCompanyId: number,
  name: string
): Promise<HotelOption> {
  const { data } = await apiClient.patch<HotelOption>(`/api/hotels/${hotelId}`, {
    hotelCompanyId,
    name,
  });
  return data;
}

export async function deactivateHotel(hotelId: number): Promise<void> {
  await apiClient.delete(`/api/hotels/${hotelId}`);
}

export async function activateHotel(hotelId: number): Promise<HotelOption> {
  const { data } = await apiClient.patch<HotelOption>(`/api/hotels/${hotelId}/activate`);
  return data;
}

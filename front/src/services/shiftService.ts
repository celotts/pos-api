import api from './api'; // CORREGIDO: Importación por defecto
import { Shift, ShiftRequest } from '../models/shift.model';
import { PageResponse } from '../models/page.model';

export const shiftService = {
  create: async (shift: ShiftRequest): Promise<Shift> => {
    const response = await api.post<Shift>('/shifts', shift);
    return response.data;
  },

  getAll: async (): Promise<Shift[]> => {
    const res = await api.get('/shifts');
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },

  getAllPaginated: async (page = 0, size = 10): Promise<PageResponse<Shift>> => {
    const response = await api.get<PageResponse<Shift>>('/shifts', {
      params: { page, size },
    });
    return response.data;
  },

  getById: async (id: string): Promise<Shift> => {
    const response = await api.get<Shift>(`/shifts/${id}`);
    return response.data;
  },

  update: async (id: string, shift: ShiftRequest): Promise<Shift> => {
    const response = await api.put<Shift>(`/shifts/${id}`, shift);
    return response.data;
  },

  close: async (id: string, endingCash: number): Promise<Shift> => {
    const response = await api.put<Shift>(`/shifts/${id}/close`, null, {
        params: { endingCash }
    });
    return response.data;
  },

  cancel: async (id: string): Promise<void> => {
    await api.put(`/shifts/${id}/cancel`);
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/shifts/${id}`);
  },
};

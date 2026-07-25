import api from './api';
import type { Tax } from '../models/catalog.model';
import type { TaxCategory } from '../enums/pos-enums';

// Data for creating/updating
export interface TaxData {
  name: string;
  percentage: number;
  taxType: TaxCategory;
}

// Re-exporting the type
export type { Tax };

export const taxService = {
  getAll: async (): Promise<Tax[]> => {
    const res = await api.get('/taxes');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<Tax> => api.get<Tax>(`/taxes/${id}`).then(res => res.data),
  create: (data: TaxData): Promise<Tax> => api.post<Tax>('/taxes', data).then(res => res.data),
  update: (id: string, data: TaxData): Promise<Tax> => api.put<Tax>(`/taxes/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/taxes/${id}`),
};

import api from './api';

export interface Tax {
  id: string;
  name: string;
  percentage: number;
  taxType: 'IVA' | 'IEPS' | 'ISR';
  createdByName?: string;
  updatedByName?: string;
}

export type TaxData = Omit<Tax, 'id' | 'createdByName' | 'updatedByName' | 'createdAt' | 'updatedAt'>;

export const taxService = {
  getAll: () => api.get<Tax[]>('/taxes').then(res => res.data),
  create: (data: TaxData) => api.post<Tax>('/taxes', data).then(res => res.data),
  update: (id: string, data: TaxData) => api.put<Tax>(`/taxes/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/taxes/${id}`),
};

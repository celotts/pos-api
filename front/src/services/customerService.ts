import api from './api';
import type { Customer } from '../models/catalog.model';

// Data for creating/updating
export type CustomerData = Omit<Customer, 'id' | 'createdAt' | 'updatedAt' | 'createdBy' | 'updatedBy'>;

// Re-exporting the type
export type { Customer };

export const customerService = {
  getAll: async (): Promise<Customer[]> => {
    const res = await api.get('/customers');
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<Customer> => api.get<Customer>(`/customers/${id}`).then(res => res.data),
  create: (data: CustomerData): Promise<Customer> => api.post<Customer>('/customers', data).then(res => res.data),
  update: (id: string, data: CustomerData): Promise<Customer> => api.put<Customer>(`/customers/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/customers/${id}`),
};

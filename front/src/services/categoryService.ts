import api from './api';
import type { Category } from '../models/catalog.model';

// The form only sends the name
export interface CategoryData {
  name: string;
}

// Re-exporting the type so other modules can import it from the service.
export type { Category };

export const categoryService = {
  getAll: async (): Promise<Category[]> => {
    const res = await api.get('/categories');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<Category> => api.get<Category>(`/categories/${id}`).then(res => res.data),
  create: (data: CategoryData): Promise<Category> => api.post<Category>('/categories', data).then(res => res.data),
  update: (id: string, data: CategoryData): Promise<Category> => api.put<Category>(`/categories/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/categories/${id}`),
};

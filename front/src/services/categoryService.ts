import api from './api';

export interface Category {
  id: string;
  name: string;
  createdByName?: string;
  updatedByName?: string;
}

export const categoryService = {
  getAll: () => api.get<Category[]>('/categories').then(res => res.data),
  create: (data: { name: string }) => api.post<Category>('/categories', data).then(res => res.data),
  update: (id: string, data: { name: string }) => api.put<Category>(`/categories/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/categories/${id}`),
};

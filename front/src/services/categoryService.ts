import api from './api';
import { Category } from '../models/category';
import { Page } from '../models/page';

export type CategoryData = {
  name: string;
};

export const categoryService = {
  getAll: async (page = 0, size = 10, sort = 'name,asc'): Promise<Page<Category>> => {
    const response = await api.get('/categories', { params: { page, size, sort } });
    return response.data;
  },
  getById: (id: string) => api.get<Category>(`/categories/${id}`).then(res => res.data),
  create: (data: CategoryData) => api.post<Category>('/categories', data).then(res => res.data),
  update: (id: string, data: CategoryData) => api.put<Category>(`/categories/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/categories/${id}`),
};

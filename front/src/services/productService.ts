import api from './api';
import { Product } from '../models/Product';

export type ProductData = Omit<Product, 'id' | 'createdAt' | 'updatedAt' | 'createdBy' | 'updatedBy'>;

export const productService = {
  getAll: async (): Promise<Product[]> => {
    const res = await api.get('/products');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return res.data.content;
    }
    return res.data;
  },
  getById: (id: string) => api.get<Product>(`/products/${id}`).then(res => res.data),
  create: (data: ProductData) => api.post<Product>('/products', data).then(res => res.data),
  update: (id: string, data: ProductData) => api.put<Product>(`/products/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/products/${id}`),
};

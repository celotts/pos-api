import api from './api';
import { Product } from '../models/Product';

export type ProductData = Omit<Product, 'id' | 'createdAt' | 'updatedAt' | 'createdBy' | 'updatedBy'>;

export const productService = {
  getAll: () => api.get<Product[]>('/products').then(res => res.data),
  create: (data: ProductData) => api.post<Product>('/products', data).then(res => res.data),
  update: (id: string, data: ProductData) => api.put<Product>(`/products/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/products/${id}`),
};

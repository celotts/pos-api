import api from './api';

export interface Product {
  id: string;
  sku: string;
  name: string;
  description?: string;
  purchasePrice: number;
  salePrice: number;
  currentStock: number;
  taxId: string;
  supplierId: string;
  categoryId: string;
  createdByName?: string;
  updatedByName?: string;
}

// Esta es la estructura exacta que tu ProductForm envía
export interface ProductData {
  sku: string;
  name: string;
  description?: string;
  purchasePrice: number;
  salePrice: number;
  currentStock: number;
  taxId: string;
  supplierId: string;
  categoryId: string;
}

export const productService = {
  getAll: () => api.get<Product[]>('/products').then(res => res.data),

  // Ahora aceptamos ProductData en lugar de solo name
  create: (data: ProductData) => api.post<Product>('/products', data).then(res => res.data),

  // Ahora aceptamos ProductData para el update
  update: (id: string, data: ProductData) => api.put<Product>(`/products/${id}`, data).then(res => res.data),

  delete: (id: string) => api.delete(`/products/${id}`),
};

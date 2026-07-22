import api from './api'; // CORREGIDO: Importación por defecto
import { Purchase, PurchaseRequest } from '../models/purchase.model';
import { PageResponse } from '../models/page.model';

export const purchaseService = {
  create: async (purchase: PurchaseRequest): Promise<Purchase> => {
    const response = await api.post<Purchase>('/purchases', purchase);
    return response.data;
  },

  getAll: async (): Promise<Purchase[]> => {
    const res = await api.get('/purchases');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },

  getAllPaginated: async (page = 0, size = 10): Promise<PageResponse<Purchase>> => {
    const response = await api.get<PageResponse<Purchase>>('/purchases', {
      params: { page, size },
    });
    return response.data;
  },

  getById: async (id: string): Promise<Purchase> => {
    const response = await api.get<Purchase>(`/purchases/${id}`);
    return response.data;
  },

  update: async (id: string, purchase: PurchaseRequest): Promise<Purchase> => {
    const response = await api.put<Purchase>(`/purchases/${id}`, purchase);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/purchases/${id}`);
  },
};

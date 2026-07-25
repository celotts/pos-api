import api from './api'; // CORREGIDO: Importación por defecto
import { AccountsPayable, AccountsPayableRequest } from '../models/accountsPayable.model';
import { PageResponse } from '../models/page.model';

export const accountsPayableService = {
  create: async (accountsPayable: AccountsPayableRequest): Promise<AccountsPayable> => {
    const response = await api.post<AccountsPayable>('/accounts-payable', accountsPayable);
    return response.data;
  },

  getAll: async (): Promise<AccountsPayable[]> => {
    const res = await api.get('/accounts-payable');
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },

  getAllPaginated: async (page = 0, size = 10): Promise<PageResponse<AccountsPayable>> => {
    const response = await api.get<PageResponse<AccountsPayable>>('/accounts-payable', {
      params: { page, size },
    });
    return response.data;
  },

  getById: async (id: string): Promise<AccountsPayable> => {
    const response = await api.get<AccountsPayable>(`/accounts-payable/${id}`);
    return response.data;
  },

  getBySupplier: async (supplierId: string, page = 0, size = 10): Promise<PageResponse<AccountsPayable>> => {
    const response = await api.get<PageResponse<AccountsPayable>>(`/accounts-payable/supplier/${supplierId}`, {
      params: { page, size },
    });
    return response.data;
  },

  getOverdue: async (asOfDate?: string, page = 0, size = 10): Promise<PageResponse<AccountsPayable>> => {
    const response = await api.get<PageResponse<AccountsPayable>>('/accounts-payable/overdue', {
      params: { asOfDate, page, size },
    });
    return response.data;
  },

  update: async (id: string, accountsPayable: AccountsPayableRequest): Promise<AccountsPayable> => {
    const response = await api.put<AccountsPayable>(`/accounts-payable/${id}`, accountsPayable);
    return response.data;
  },

  markAsPaid: async (id: string, amountPaid: number): Promise<AccountsPayable> => {
    const response = await api.put<AccountsPayable>(`/accounts-payable/${id}/mark-paid`, null, {
        params: { amountPaid }
    });
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/accounts-payable/${id}`);
  },
};

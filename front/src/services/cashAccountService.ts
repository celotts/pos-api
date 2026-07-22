import api from './api'; // CORREGIDO: Importación por defecto
import { CashAccount, CashAccountRequest } from '../models/cashAccount.model';

export const cashAccountService = {
  create: async (cashAccount: CashAccountRequest): Promise<CashAccount> => {
    const response = await api.post<CashAccount>('/cash-accounts', cashAccount);
    return response.data;
  },

  getAll: async (): Promise<CashAccount[]> => {
    const response = await api.get<CashAccount[]>('/cash-accounts');
    return response.data;
  },

  getById: async (id: string): Promise<CashAccount> => {
    const response = await api.get<CashAccount>(`/cash-accounts/${id}`);
    return response.data;
  },

  update: async (id: string, cashAccount: CashAccountRequest): Promise<CashAccount> => {
    const response = await api.put<CashAccount>(`/cash-accounts/${id}`, cashAccount);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/cash-accounts/${id}`);
  },
};

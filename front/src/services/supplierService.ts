import api from './api';

export interface Supplier {
  id: string;
  rfc: string;
  businessName: string;
  taxRegimen: string;
  contactEmail: string;
  createdByName?: string;
  updatedByName?: string;
}

export type SupplierData = Omit<Supplier, 'id' | 'createdByName' | 'updatedByName' | 'createdAt' | 'updatedAt'>;

export const supplierService = {
  getAll: () => api.get<Supplier[]>('/suppliers').then(res => res.data),
  create: (data: SupplierData) => api.post<Supplier>('/suppliers', data).then(res => res.data),
  update: (id: string, data: SupplierData) => api.put<Supplier>(`/suppliers/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/suppliers/${id}`),
};

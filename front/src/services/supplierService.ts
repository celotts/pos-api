import api from './api';

// Define the shape based on the form and page components
// We assume the BaseAuditedEntity properties exist from the backend
export interface Supplier {
  id: string;
  rfc: string;
  businessName: string;
  taxRegimen: string;
  contactEmail: string;
  createdAt: string;
  updatedAt?: string;
}

// Data for creating/updating
export type SupplierData = Omit<Supplier, 'id' | 'createdAt' | 'updatedAt'>;

export const supplierService = {
  getAll: async (): Promise<Supplier[]> => {
    const res = await api.get('/suppliers');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<Supplier> => api.get<Supplier>(`/suppliers/${id}`).then(res => res.data),
  create: (data: SupplierData): Promise<Supplier> => api.post<Supplier>('/suppliers', data).then(res => res.data),
  update: (id: string, data: SupplierData): Promise<Supplier> => api.put<Supplier>(`/suppliers/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/suppliers/${id}`),
};

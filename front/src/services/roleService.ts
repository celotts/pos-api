import api from './api';
import type { Role } from '../models/catalog.model';

// Data for creating/updating
export interface RoleData {
  name: string;
}

// Re-exporting the type
export type { Role };

export const roleService = {
  getAll: async (): Promise<Role[]> => {
    const res = await api.get('/roles');
    // Handle Spring Boot's typical pagination wrapper
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<Role> => api.get<Role>(`/roles/${id}`).then(res => res.data),
  create: (data: RoleData): Promise<Role> => api.post<Role>('/roles', data).then(res => res.data),
  update: (id: string, data: RoleData): Promise<Role> => api.put<Role>(`/roles/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/roles/${id}`),
};

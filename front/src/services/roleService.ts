import api from './api';

export interface Role {
  id: string;
  name: string;
  createdByName?: string;
  updatedByName?: string;
}

export const roleService = {
  getAll: () => api.get<Role[]>('/roles').then(res => res.data),
  create: (data: { name: string }) => api.post<Role>('/roles', data).then(res => res.data),
  update: (id: string, data: { name: string }) => api.put<Role>(`/roles/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/roles/${id}`),
};

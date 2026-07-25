import api from './api';
import type { User } from '../models/catalog.model';

// Data for creating/updating
// Password is optional for updates
export interface UserData {
  email: string;
  fullName: string;
  roleId: string;
  password?: string;
}

// Re-exporting the type
export type { User };

export const userService = {
  getAll: async (): Promise<User[]> => {
    const res = await api.get('/users');
    // Assuming the backend sends the joined 'role' object
    if (res.data && typeof res.data === 'object' && 'content' in res.data) {
      return (res.data as any).content;
    }
    return res.data;
  },
  getById: (id: string): Promise<User> => api.get<User>(`/users/${id}`).then(res => res.data),
  create: (data: UserData): Promise<User> => api.post<User>('/users', data).then(res => res.data),
  update: (id: string, data: Partial<UserData>): Promise<User> => api.put<User>(`/users/${id}`, data).then(res => res.data),
  delete: (id: string): Promise<void> => api.delete(`/users/${id}`),
};

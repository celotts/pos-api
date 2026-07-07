import api from './api';
import { Role } from './roleService';

export interface User {
  id: string;
  email: string;
  fullName: string;
  isActive: boolean;
  role: Role;
}

// El tipo de dato que se envía al crear o actualizar un usuario
export type UserData = {
  email: string;
  fullName: string;
  password?: string; // La contraseña es opcional en la actualización
  roleId: string;
};

export const userService = {
  getAll: () => api.get<User[]>('/users').then(res => res.data),
  create: (data: UserData) => api.post<User>('/users', data).then(res => res.data),
  update: (id: string, data: UserData) => api.put<User>(`/users/${id}`, data).then(res => res.data),
  delete: (id: string) => api.delete(`/users/${id}`),
};

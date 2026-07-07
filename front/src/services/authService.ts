import api from './api';

export interface LoginCredentials {
  email: string;
  password: string;
}

export const authService = {
  login: (credentials: LoginCredentials) => api.post<{ token: string }>('/auth/login', credentials).then(res => res.data),
};

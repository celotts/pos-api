import api from './api';

export interface LoginCredentials {
  email: string;
  password: string;
}

export const authService = {
  login: async (credentials: LoginCredentials) => { // AÑADIDO: async para usar await
    console.log('authService.login called with credentials:', credentials);
    try {
      const response = await api.post<{ token: string }>('/auth/authenticate', credentials);
      console.log('API call successful, response:', response); // AÑADIDO
      return response.data;
    } catch (error) {
      console.error('Error during API call in authService.login:', error); // AÑADIDO
      throw error; // Re-lanzar el error para que LoginPage lo maneje
    }
  },
};

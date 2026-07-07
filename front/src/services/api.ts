import axios from 'axios';
import { store } from '../store';

const api = axios.create({
  baseURL: '/api/v1',
});

// Interceptor para añadir el token a cada petición
api.interceptors.request.use(
  (config) => {
    const token = store.getState().auth.token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;

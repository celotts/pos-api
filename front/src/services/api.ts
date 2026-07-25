import axios from 'axios';
import { store } from '../store';
import { logOut } from '../store/slices/authSlice';

// CORREGIDO: La baseURL debe apuntar al backend con el puerto y versión correctos
const api = axios.create({
  baseURL: 'http://localhost:9090/api/v1', // CORREGIDO
});

// Interceptor para AÑADIR el token a cada petición
api.interceptors.request.use(
  (config) => {
    const token = store.getState().auth.token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para MANEJAR errores de autenticación en las respuestas
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && [401, 403].includes(error.response.status)) {
      store.dispatch(logOut());
      window.location.href = '/login?sessionExpired=true';
    }
    return Promise.reject(error);
  }
);

export default api;

import api from './api';
import { UserData as BackendUserResponse } from '../store/slices/authSlice'; // Importar UserData del slice

/**
 * Define la estructura de las credenciales que se envían al backend.
 */
export interface UserCredentials {
  email: string;
  password: string; // La contraseña siempre debe ser requerida para el login
}

/**
 * Define la respuesta esperada del endpoint de login.
 * Debe coincidir con LoginResponse del backend.
 */
export interface AuthResponse {
  token: string;
  user: BackendUserResponse; // Usar la UserData definida en authSlice
}

const login = async (credentials: UserCredentials): Promise<AuthResponse> => {
  // CORREGIDO: Usar el endpoint correcto /auth/authenticate
  const response = await api.post<AuthResponse>('/auth/authenticate', credentials);
  return response.data;
};

const authService = {
  login,
};

export default authService;

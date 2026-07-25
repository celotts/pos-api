import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import authService, { UserCredentials } from '../../services/authService';
import type { NavigateFunction } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

// Define la interfaz para los datos del usuario que vienen del backend (UserResponse)
export interface UserData {
  id: string;
  email: string;
  fullName: string;
  roleName: string; // Nombre del rol (ej. "ADMIN")
  isActive: boolean;
  createdAt: string; // Instant del backend
  updatedAt: string | null; // Instant del backend
}

// Define la interfaz para el estado de autenticación
interface AuthState {
  user: UserData | null;
  token: string | null;
  loading: boolean;
  error: string | null;
  loginAttempts: number;
  isBlocked: boolean; // Nueva propiedad para el bloqueo
}

// Define la interfaz para el token decodificado (lo que viene dentro del JWT)
interface DecodedToken {
  sub: string; // email
  roles: string[];
  exp: number; // expiration timestamp
}

// Función para cargar el estado desde localStorage al inicio de la aplicación
const loadState = (): AuthState => {
  try {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (!token || !storedUser) return { user: null, token: null, loading: false, error: null, loginAttempts: 0, isBlocked: false };

    const decoded = jwtDecode<DecodedToken>(token);

    // Verificar si el token ha expirado
    if (decoded.exp * 1000 < Date.now()) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return { user: null, token: null, loading: false, error: null, loginAttempts: 0, isBlocked: false };
    }

    const user: UserData = JSON.parse(storedUser);

    return { user, token, loading: false, error: null, loginAttempts: 0, isBlocked: false };
  } catch (error) {
    console.error("Error loading auth state from localStorage:", error);
    localStorage.removeItem('token');
    localStorage.removeItem('user'); // Limpiar también el usuario si hay error
    return { user: null, token: null, loading: false, error: null, loginAttempts: 0, isBlocked: false };
  }
};


// Define el estado inicial usando loadState()
const initialState: AuthState = loadState();

// Interfaz para el payload del thunk de login
interface LoginCredentialsPayload extends UserCredentials {
  from: string;
  navigate: NavigateFunction; // La función navigate se pasa al thunk
}

// Thunk asíncrono para el login
export const login = createAsyncThunk(
  'auth/login',
  async (payload: LoginCredentialsPayload, { rejectWithValue }) => {
    try {
      const { email, password, from, navigate } = payload; // Extraer navigate y from
      // authService.login ahora devuelve { token: string, user: UserData }
      const response = await authService.login({ email, password });

      localStorage.setItem('token', response.token); // Guardar token
      localStorage.setItem('user', JSON.stringify(response.user)); // Guardar datos del usuario

      navigate(from, { replace: true }); // CORREGIDO: Navegar aquí, dentro del thunk

      // Retornar solo datos serializables para el reducer
      return { token: response.token, user: response.user };
    } catch (error: any) {
      if (error.response && error.response.data) {
        return rejectWithValue(error.response.data.message || 'Error de autenticación');
      }
      return rejectWithValue(error.message);
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logOut: (state) => {
      state.user = null;
      state.token = null;
      state.loading = false;
      state.error = null;
      state.loginAttempts = 0;
      state.isBlocked = false; // Resetear también el bloqueo
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      console.log('authSlice - logOut: State cleared');
    },
    resetLoginAttempts: (state) => {
      state.loginAttempts = 0;
      state.isBlocked = false; // Resetear también el bloqueo
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.token;
        state.user = action.payload.user;
        state.loginAttempts = 0;
        state.isBlocked = false; // Asegurarse de que no esté bloqueado al iniciar sesión
        console.log('authSlice - login.fulfilled: State updated', { token: state.token, user: state.user });
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        state.user = null;
        state.token = null;
        // Solo incrementar si no está bloqueado
        if (!state.isBlocked) {
          state.loginAttempts += 1;
          if (state.loginAttempts >= 3) {
            state.isBlocked = true;
          }
        }
        console.error('authSlice - login.rejected: Error', action.payload);
      });
  },
});

export const { logOut, resetLoginAttempts } = authSlice.actions;

export default authSlice.reducer;

export const selectCurrentUser = (state: { auth: AuthState }) => state.auth.user;
export const selectIsAuthenticated = (state: { auth: AuthState }) => {
  console.log('selectIsAuthenticated - Full state:', state);
  console.log('selectIsAuthenticated - state.auth:', state.auth);
  console.log('selectIsAuthenticated - state.auth.token:', state.auth.token);
  return !!state.auth.token;
};
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.loading;
export const selectLoginAttempts = (state: { auth: AuthState }) => state.auth.loginAttempts;
export const selectIsBlocked = (state: { auth: AuthState }) => state.auth.isBlocked;
export const selectAuthError = (state: { auth: AuthState }) => state.auth.error;

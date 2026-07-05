import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '..';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string; // Email
  roles: string[];
}

interface AuthState {
  user: { email: string; roles: string[] } | null;
  token: string | null;
}

// Función para inicializar el estado desde localStorage
const loadState = (): AuthState => {
  try {
    const token = localStorage.getItem('token');
    if (token === null) {
      return { user: null, token: null };
    }
    const decoded = jwtDecode<DecodedToken>(token);
    return { user: { email: decoded.sub, roles: decoded.roles || [] }, token };
  } catch (error) {
    // Si el token es inválido o ha expirado, limpiamos
    localStorage.removeItem('token');
    return { user: null, token: null };
  }
};

const authSlice = createSlice({
  name: 'auth',
  initialState: loadState(),
  reducers: {
    setCredentials: (state, action: PayloadAction<{ token: string }>) => {
      const { token } = action.payload;
      const decoded = jwtDecode<DecodedToken>(token);

      state.token = token;
      state.user = { email: decoded.sub, roles: decoded.roles || [] };

      localStorage.setItem('token', token);
    },
    logOut: (state) => {
      state.user = null;
      state.token = null;
      localStorage.removeItem('token');
    },
  },
});

export const { setCredentials, logOut } = authSlice.actions;
export default authSlice.reducer;

export const selectCurrentUser = (state: RootState) => state.auth.user;
export const selectIsAuthenticated = (state: RootState) => state.auth.token !== null;

import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '..';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string;
  roles: string[];
  exp: number;
}

interface AuthState {
  user: { email: string; roles: string[] } | null;
  token: string | null;
}

const loadState = (): AuthState => {
  try {
    const token = localStorage.getItem('token');
    if (!token) return { user: null, token: null };

    const decoded = jwtDecode<DecodedToken>(token);
    if (decoded.exp * 1000 < Date.now()) {
      localStorage.removeItem('token');
      return { user: null, token: null };
    }

    const roles = decoded.roles.map(role => role.replace('ROLE_', ''));
    return { user: { email: decoded.sub, roles }, token };
  } catch (error) {
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
      const roles = decoded.roles.map(role => role.replace('ROLE_', ''));

      state.token = token;
      state.user = { email: decoded.sub, roles };

      localStorage.setItem('token', token);
      console.log('authSlice - setCredentials: State updated', { token: state.token, user: state.user }); // <-- AÑADIDO
    },
    logOut: (state) => {
      state.user = null;
      state.token = null;
      localStorage.removeItem('token');
      console.log('authSlice - logOut: State cleared'); // <-- AÑADIDO
    },
  },
});

export const { setCredentials, logOut } = authSlice.actions;
export default authSlice.reducer;

export const selectCurrentUser = (state: RootState) => state.auth.user;
export const selectIsAuthenticated = (state: RootState) => {
  console.log('selectIsAuthenticated - state.auth:', state.auth); // <-- AÑADIDO
  console.log('selectIsAuthenticated - state.auth.token:', state.auth.token); // <-- AÑADIDO
  return state.auth.token !== null;
};

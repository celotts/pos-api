import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '../../store' // Asegúrate que la ruta a tu store sea correcta

interface AuthState {
  token: string | null
  isAuthenticated: boolean
  user: { email: string } | null // Puedes expandir esto con más datos del usuario
}

const initialState: AuthState = {
  token: localStorage.getItem('token'), // Recuperamos el token si existe
  isAuthenticated: !!localStorage.getItem('token'),
  user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (state, { payload: { user, token } }: PayloadAction<{ user: { email: string }; token: string }>) => {
      state.user = user
      state.token = token
      state.isAuthenticated = true
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout: (state) => {
      state.user = null
      state.token = null
      state.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})

export const { setCredentials, logout } = authSlice.actions

export default authSlice.reducer

export const selectIsAuthenticated = (state: RootState) => state.auth.isAuthenticated
export const selectAuthToken = (state: RootState) => state.auth.token

// Deberás agregar este slice a tu `store` principal.
// Ejemplo en store.ts:
//
// import authReducer from '../features/auth/authSlice';
//
// export const store = configureStore({
//   reducer: {
//     auth: authReducer,
//     // ... otros reducers
//   },
// });

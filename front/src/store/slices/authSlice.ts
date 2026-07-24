import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit'
import authService, { UserCredentials, UserData } from '../../services/authService'
import type { NavigateFunction } from 'react-router-dom'
import { jwtDecode } from 'jwt-decode'

// Define la forma del estado de autenticación
interface AuthState {
  user: UserData | null
  token: string | null
  loading: boolean
  error: string | null
}

// Define el estado inicial
const initialState: AuthState = {
  user: null,
  token: null,
  loading: false,
  error: null,
}

interface LoginCredentials extends UserCredentials {
  from: string
  navigate: NavigateFunction
}

// Thunk asíncrono para el login
export const login = createAsyncThunk('auth/login', async (payload: LoginCredentials, { rejectWithValue }) => {
  try {
    // Extraemos solo las credenciales para la API
    const { email, password } = payload
    const data = await authService.login({ email, password })
    // Guardamos el token en localStorage para persistir la sesión
    localStorage.setItem('token', data.token)
    return { ...data, from: payload.from, navigate: payload.navigate }
  } catch (error: any) {
    // Si hay un error en la respuesta de la API, lo pasamos
    if (error.response && error.response.data) {
      return rejectWithValue(error.response.data.message || 'Error de autenticación')
    }
    return rejectWithValue(error.message)
  }
})

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Acción para cerrar sesión
    logOut: (state) => {
      state.user = null
      state.token = null
      localStorage.removeItem('token')
    },
    // Acción para restaurar el estado desde localStorage
    restoreAuthState: (state) => {
      const token = localStorage.getItem('token')
      if (token) {
        try {
          const decodedUser: UserData = jwtDecode(token)
          // Aquí podrías añadir una comprobación de la expiración del token
          state.user = decodedUser
          state.token = token
        } catch (error) {
          // Si el token es inválido, lo limpiamos
          localStorage.removeItem('token')
          state.user = null
          state.token = null
        }
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false
        state.token = action.payload.token
        state.user = jwtDecode(action.payload.token)
        action.payload.navigate(action.payload.from, { replace: true })
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload as string
        state.user = null
        state.token = null
      })
  },
})

export const { logOut, restoreAuthState } = authSlice.actions

// Selectores para acceder al estado desde los componentes
export const selectCurrentUser = (state: { auth: AuthState }) => state.auth.user
export const selectIsAuthenticated = (state: { auth: AuthState }) => !!state.auth.token
export const selectAuthLoading = (state: { auth: AuthState }) => state.auth.loading

export default authSlice.reducer

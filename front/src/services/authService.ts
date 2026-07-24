import api from './api'

/**
 * Define la estructura de las credenciales que se envían al backend.
 */
export interface UserCredentials {
  email: string
  password?: string
}

/**
 * Define la estructura de los datos del usuario que se obtienen del token JWT.
 */
export interface UserData {
  id: string
  sub: string // El 'subject' del token, generalmente el email o username
  fullName: string
  roles: string[]
  iat: number
  exp: number
}

/**
 * Define la respuesta esperada del endpoint de login.
 */
interface AuthResponse {
  token: string
}

const login = async (credentials: UserCredentials): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', credentials)
  return response.data
}

const authService = {
  login,
}

export default authService

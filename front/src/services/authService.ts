import axios from 'axios';

const API_URL = '/api/v1/auth';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

const login = async (credentials: LoginCredentials): Promise<LoginResponse> => {
  const response = await axios.post<LoginResponse>(`${API_URL}/login`, credentials);
  return response.data;
};

const authService = {
  login,
};

export default authService;

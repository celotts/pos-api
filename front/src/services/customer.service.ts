import axios from 'axios';
import { Customer, CustomerPayload } from '../models/customer.model';
import { getToken } from './auth.service';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const customerApi = axios.create({
  baseURL: `${API_URL}/customers`,
});

// Interceptor para añadir el token de autenticación
customerApi.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const fetchCustomers = async (): Promise<Customer[]> => {
  const response = await customerApi.get('/');
  return response.data;
};

export const createCustomer = async (customer: CustomerPayload): Promise<Customer> => {
  const response = await customerApi.post('/', customer);
  return response.data;
};

export const updateCustomer = async (id: string, customer: CustomerPayload): Promise<Customer> => {
  const response = await customerApi.put(`/${id}`, customer);
  return response.data;
};

export const deleteCustomer = async (id: string): Promise<void> => {
  await customerApi.delete(`/${id}`);
};

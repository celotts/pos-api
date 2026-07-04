import axios from 'axios';
import { store } from '../store';

// Interfaz para el objeto Category que recibimos de la API
export interface Category {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string | null;
  createdByName: string;
  updatedByName: string | null;
}

// Interfaz para crear/actualizar una categoría
export interface CategoryData {
  name: string;
}

const API_URL = '/api/v1/categories';

// Helper para obtener el token y configurar los headers
const getAuthHeaders = () => {
  const token = store.getState().auth.token;
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

const getAllCategories = async (): Promise<Category[]> => {
  const response = await axios.get<Category[]>(API_URL, getAuthHeaders());
  return response.data;
};

const createCategory = async (categoryData: CategoryData): Promise<Category> => {
  const response = await axios.post<Category>(API_URL, categoryData, getAuthHeaders());
  return response.data;
};

const updateCategory = async (id: string, categoryData: CategoryData): Promise<Category> => {
  const response = await axios.put<Category>(`${API_URL}/${id}`, categoryData, getAuthHeaders());
  return response.data;
};

const deleteCategory = async (id: string): Promise<void> => {
  await axios.delete(`${API_URL}/${id}`, getAuthHeaders());
};

const categoryService = {
  getAllCategories,
  createCategory,
  updateCategory,
  deleteCategory,
};

export default categoryService;

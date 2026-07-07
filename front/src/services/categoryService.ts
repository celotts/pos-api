import api from './api';

// Mantenemos tu interfaz original extendiéndola para compatibilidad con el formulario
export interface Category {
  id: string;
  name: string;
  createdByName?: string;
  updatedByName?: string;
}

// Interfaz estandarizada para los selectores (CatalogItem)
export interface CategoryOption {
  id: string;
  name: string;
}

export const categoryService = {
  // Obtiene todas las categorías (retorna Category[], compatible con Selects)
  getAll: () => api.get<Category[]>('/categories').then(res => res.data),

  // Create ahora espera el objeto completo según tu lógica de dominio
  create: (data: { name: string }) => api.post<Category>('/categories', data).then(res => res.data),

  // Update
  update: (id: string, data: { name: string }) => api.put<Category>(`/categories/${id}`, data).then(res => res.data),

  // Delete
  delete: (id: string) => api.delete(`/categories/${id}`),
};

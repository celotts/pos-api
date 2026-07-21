import { Product } from '../features/products/types'
import { store } from '../store' // Asegúrate que la ruta a tu store sea correcta

const API_BASE_URL = 'http://localhost:8080'

const getHeaders = () => {
  const token = store.getState().auth.token // Obtenemos el token del store de Redux

  return {
    'Content-Type': 'application/json',
    Authorization: token ? `Bearer ${token}` : '',
  }
}

export const getProducts = async (): Promise<Product[]> => {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'GET',
    headers: getHeaders(),
  })
  if (!response.ok) {
    throw new Error('Failed to fetch products')
  }
  return response.json()
}

export const getProductById = async (id: string): Promise<Product> => {
  const response = await fetch(`${API_BASE_URL}/products/${id}`, {
    method: 'GET',
    headers: getHeaders(),
  })
  if (!response.ok) {
    throw new Error(`Failed to fetch product with id ${id}`)
  }
  return response.json()
}

export const createProduct = async (productData: Omit<Product, 'id'>): Promise<Product> => {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(productData),
  })
  if (!response.ok) {
    throw new Error('Failed to create product')
  }
  return response.json()
}

// Aquí podrías añadir las funciones para updateProduct y deleteProduct
// siguiendo el mismo patrón.

import { Product } from '../features/products/types'

// Las credenciales del admin. En una aplicación real, esto vendría de un estado de autenticación.
const AUTH_TOKEN = btoa('admin@posapi.com:admin') // Codificado en Base64
const API_BASE_URL = 'http://localhost:8080'

const getHeaders = () => {
  return {
    'Content-Type': 'application/json',
    Authorization: `Basic ${AUTH_TOKEN}`,
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

export interface Product {
  id: string // Asumiendo que el backend devuelve el UUID como string
  sku: string
  name: string
  description: string
  purchasePrice: number
  salePrice: number
  currentStock: number
  taxId?: string | null
  supplierId?: string | null
}

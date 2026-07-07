export interface Product {
  id: string;
  sku: string;
  name: string;
  description?: string;
  purchasePrice: number;
  salePrice: number;
  currentStock: number;
  categoryId: string;
  taxId?: string;
  supplierId?: string;
  createdAt: string;
  updatedAt?: string;
  createdBy: string;
  updatedBy?: string;
}

export interface PurchaseItem {
  id: string;
  purchaseId: string;
  productId: string;
  productName: string;
  productSku: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  createdByName: string;
  updatedByName: string;
  deletedByName?: string;
}

export interface Purchase {
  id: string;
  supplierId: string;
  purchaseDate: string;
  totalAmount: number;
  totalTaxAmount: number;
  status: string;
  paymentStatus: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  createdByUserId: string;
  updatedByUserId: string;
  deletedByUserId?: string;
  createdByName: string;
  updatedByName: string;
  deletedByName?: string;
  items: PurchaseItem[];
}

export interface PurchaseRequest {
  supplierId: string;
  purchaseDate: string;
  items: {
    productId: string;
    quantity: number;
    unitPrice: number;
  }[];
}

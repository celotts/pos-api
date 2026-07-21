import { BaseAuditedEntity } from './audit.model';
import { TaxCategory, ShiftStatus, SaleStatus, PaymentStatus } from '../enums/pos-enums';

export interface Role extends BaseAuditedEntity {
  name: string;
}

export interface User extends BaseAuditedEntity {
  email: string;
  fullName: string;
  isActive: boolean;
  roleId: string;
  role?: Role;
}

export interface Customer extends BaseAuditedEntity {
  fullName: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  rfc?: string;
}

export interface Supplier extends BaseAuditedEntity {
  name: string;
  email?: string;
}

export interface Category extends BaseAuditedEntity {
  name: string;
}

export interface Tax extends BaseAuditedEntity {
  name: string;
  percentage: number;
  taxType: TaxCategory;
}

export interface Product extends BaseAuditedEntity {
  sku: string;
  name: string;
  description?: string;
  purchasePrice: number;
  salePrice: number;
  currentStock: number;
  categoryId?: string | null;
  taxId?: string | null;
  supplierId?: string | null;
  // Propiedades opcionales cargadas por JOINs del Backend
  category?: Category;
  tax?: Tax;
  supplier?: Supplier;
}

export interface PosTerminal extends BaseAuditedEntity {
  name: string;
  location?: string;
  isActive: boolean;
}

export interface Shift extends BaseAuditedEntity {
  userId: string;
  posTerminalId: string;
  startTime: string;
  endTime?: string | null;
  startingCash: number;
  endingCash?: number | null;
  status: ShiftStatus;
}

export interface Sale extends BaseAuditedEntity {
  customerId?: string | null;
  saleDate: string;
  totalAmount: number;
  totalTaxAmount: number;
  discountAmount: number;
  status: SaleStatus;
  paymentStatus: PaymentStatus;
  posTerminalId?: string | null;
  shiftId?: string | null;
}

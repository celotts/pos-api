export enum AuditAction {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
  LOGIN = 'LOGIN',
  LOGOUT = 'LOGOUT',
  VIEW = 'VIEW'
}

export enum ShiftStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

export enum SaleStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum PaymentStatus {
  UNPAID = 'UNPAID',
  PAID = 'PAID',
  PARTIALLY_PAID = 'PARTIALLY_PAID'
}

export enum PurchaseStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum ArApStatus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  OVERDUE = 'OVERDUE'
}

export enum CashAccountType {
  CASH = 'CASH',
  BANK = 'BANK',
  CREDIT_CARD = 'CREDIT_CARD'
}

export enum TaxCategory {
  IVA = 'IVA',
  IEPS = 'IEPS',
  ISR = 'ISR'
}

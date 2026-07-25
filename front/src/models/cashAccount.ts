export const CashAccountType = {
  CASH: 'CASH',
  BANK: 'BANK',
  CREDIT_CARD: 'CREDIT_CARD',
} as const;

export type CashAccountType = typeof CashAccountType[keyof typeof CashAccountType];

export interface CashAccount {
  id: string;
  name: string;
  accountType: CashAccountType;
  currentBalance: number;
  currency: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  createdByName: string;
  updatedByName: string;
  deletedByName?: string;
}

export interface CashAccountRequest {
  name: string;
  accountType: CashAccountType;
  initialBalance: number;
  currency: string;
}

export interface AccountsPayable {
  id: string;
  purchaseId: string;
  supplierId: string;
  originalAmount: number;
  outstandingAmount: number;
  dueDate: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  createdByName: string;
  updatedByName: string;
  deletedByName?: string;
}

export interface AccountsPayableRequest {
  purchaseId: string;
  supplierId: string;
  originalAmount: number;
  outstandingAmount: number;
  dueDate: string;
}

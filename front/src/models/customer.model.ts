// Define la estructura principal de un cliente
export interface Customer {
  id: string;
  fullName: string;
  email?: string;
  phone?: string;
  address?: string;
  createdAt: string;
  updatedAt?: string;
  createdByName?: string;
  updatedByName?: string;
}

// Define la estructura para crear o actualizar un cliente
export type CustomerPayload = Omit<Customer, 'id' | 'createdAt' | 'updatedAt' | 'createdByName' | 'updatedByName'>;

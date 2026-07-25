import { BaseAuditedEntity } from './BaseAuditedEntity';

export interface Category extends BaseAuditedEntity {
  name: string;
  createdByName?: string;
  updatedByName?: string;
  deletedByName?: string;
}

export interface Page<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

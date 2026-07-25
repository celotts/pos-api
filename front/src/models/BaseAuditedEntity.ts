export interface BaseAuditedEntity {
  id: string; // UUID v4
  createdAt?: string; // TIMESTAMPTZ ISO String
  updatedAt?: string | null;
  deletedAt?: string | null;
  createdByUserId?: string | null;
  updatedByUserId?: string | null;
  deletedByUserId?: string | null;
  createdByRoleId?: string | null;
  updatedByRoleId?: string | null;
  deletedByRoleId?: string | null;
}

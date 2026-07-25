export interface Shift {
  id: string;
  userId: string;
  posTerminalId: string;
  startTime: string;
  endTime?: string;
  startingCash: number;
  endingCash?: number;
  status: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  createdByName: string;
  updatedByName: string;
  deletedByName?: string;
}

export interface ShiftRequest {
  userId: string;
  posTerminalId: string;
  startingCash: number;
  status?: string;
}

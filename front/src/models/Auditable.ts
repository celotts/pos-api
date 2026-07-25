export interface Shift extends Auditable {
   userId: UUID;
   posTerminalId: UUID;
   startTime: string;
   endTime?: string;
   startingCash: number;
   endingCash?: number;
   status: ShiftStatus;
 }

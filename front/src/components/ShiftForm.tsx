import React, { useState, useEffect } from 'react';
import { Shift, ShiftRequest } from '../models/shift.model';
import { User, userService } from '../services/userService';
import Button from './ui/Button';

interface ShiftFormProps {
  onSubmit: (data: ShiftRequest) => void;
  onCancel: () => void;
  initialData?: Shift | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const ShiftForm: React.FC<ShiftFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [userId, setUserId] = useState(initialData?.userId || '');
  const [posTerminalId, setPosTerminalId] = useState(initialData?.posTerminalId || '');
  const [startingCash, setStartingCash] = useState(initialData?.startingCash || 0);

  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    userService.getAll().then(setUsers);
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!userId || !posTerminalId || startingCash < 0) {
      alert('Please select a user, enter a POS Terminal ID, and a valid starting cash amount.');
      return;
    }
    const shiftRequest: ShiftRequest = {
      userId,
      posTerminalId,
      startingCash,
    };
    onSubmit(shiftRequest);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="user" className="block text-sm font-medium text-gray-700">User</label>
        <select id="user" value={userId} onChange={e => setUserId(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
          <option value="">Select a user</option>
          {users.map(u => <option key={u.id} value={u.id}>{u.fullName}</option>)}
        </select>
      </div>
      <div>
        <label htmlFor="posTerminalId" className="block text-sm font-medium text-gray-700">POS Terminal ID</label>
        <input type="text" id="posTerminalId" value={posTerminalId} onChange={e => setPosTerminalId(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>
      <div>
        <label htmlFor="startingCash" className="block text-sm font-medium text-gray-700">Starting Cash</label>
        <input type="number" id="startingCash" value={startingCash} onChange={e => setStartingCash(Number(e.target.value))} required min="0" step="0.01"
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>


      {apiError && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
          <strong className="font-bold">Error!</strong>
          <span className="block sm:inline"> {apiError.message}</span>
          {apiError.errors && (
            <ul className="list-disc pl-5 mt-2">
              {apiError.errors.map((error, index) => <li key={index}>{error}</li>)}
            </ul>
          )}
        </div>
      )}

      <div className="flex justify-end space-x-2 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={isSubmitting}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Submitting...' : 'Submit'}</Button>
      </div>
    </form>
  );
};

export default ShiftForm;

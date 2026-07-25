import React, { useState } from 'react';
import { CashAccount, CashAccountRequest, CashAccountType } from '../models/cashAccount'; // CORREGIDO: Nombre del archivo
import Button from './ui/Button';

interface CashAccountFormProps {
  onSubmit: (data: CashAccountRequest) => void;
  onCancel: () => void;
  initialData?: CashAccount | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const CashAccountForm: React.FC<CashAccountFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [name, setName] = useState(initialData?.name || '');
  const [accountType, setAccountType] = useState<CashAccountType>(initialData?.accountType || 'CASH');
  const [initialBalance, setInitialBalance] = useState(initialData?.currentBalance || 0);
  const [currency, setCurrency] = useState(initialData?.currency || 'USD');


  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !accountType || initialBalance < 0 || !currency) {
      alert('Please fill all fields with valid values.');
      return;
    }
    const request: CashAccountRequest = {
      name,
      accountType,
      initialBalance,
      currency,
    };
    onSubmit(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700">Name</label>
        <input type="text" id="name" value={name} onChange={e => setName(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>
      <div>
        <label htmlFor="accountType" className="block text-sm font-medium text-gray-700">Account Type</label>
        <select id="accountType" value={accountType} onChange={e => setAccountType(e.target.value as CashAccountType)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
          {Object.values(CashAccountType).map(type => <option key={type} value={type}>{type}</option>)}
        </select>
      </div>
      <div>
        <label htmlFor="initialBalance" className="block text-sm font-medium text-gray-700">Initial Balance</label>
        <input type="number" id="initialBalance" value={initialBalance} onChange={e => setInitialBalance(Number(e.target.value))} required min="0" step="0.01"
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>
      <div>
        <label htmlFor="currency" className="block text-sm font-medium text-gray-700">Currency</label>
        <input type="text" id="currency" value={currency} onChange={e => setCurrency(e.target.value)} required maxLength={3}
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

export default CashAccountForm;

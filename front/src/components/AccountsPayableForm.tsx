import React, { useState, useEffect } from 'react';
import { AccountsPayable, AccountsPayableRequest } from '../models/accountsPayable.model';
import { Purchase, purchaseService } from '../services/purchaseService';
import { Supplier, supplierService } from '../services/supplierService';
import Button from './ui/Button';

interface AccountsPayableFormProps {
  onSubmit: (data: AccountsPayableRequest) => void;
  onCancel: () => void;
  initialData?: AccountsPayable | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const AccountsPayableForm: React.FC<AccountsPayableFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [purchaseId, setPurchaseId] = useState(initialData?.purchaseId || '');
  const [supplierId, setSupplierId] = useState(initialData?.supplierId || '');
  const [originalAmount, setOriginalAmount] = useState(initialData?.originalAmount || 0);
  const [outstandingAmount, setOutstandingAmount] = useState(initialData?.outstandingAmount || 0);
  const [dueDate, setDueDate] = useState(initialData?.dueDate || new Date().toISOString().split('T')[0]);

  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);

  useEffect(() => {
    purchaseService.getAll().then(setPurchases);
    supplierService.getAll().then(setSuppliers);
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!purchaseId || !supplierId || originalAmount <= 0) {
      alert('Please select a purchase, a supplier and enter a valid original amount.');
      return;
    }
    const request: AccountsPayableRequest = {
      purchaseId,
      supplierId,
      originalAmount,
      outstandingAmount,
      dueDate,
    };
    onSubmit(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="purchase" className="block text-sm font-medium text-gray-700">Purchase</label>
        <select id="purchase" value={purchaseId} onChange={e => setPurchaseId(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
          <option value="">Select a purchase</option>
          {purchases.map(p => <option key={p.id} value={p.id}>{p.id}</option>)}
        </select>
      </div>
      <div>
        <label htmlFor="supplier" className="block text-sm font-medium text-gray-700">Supplier</label>
        <select id="supplier" value={supplierId} onChange={e => setSupplierId(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
          <option value="">Select a supplier</option>
          {suppliers.map(s => <option key={s.id} value={s.id}>{s.businessName}</option>)}
        </select>
      </div>
      <div>
        <label htmlFor="originalAmount" className="block text-sm font-medium text-gray-700">Original Amount</label>
        <input type="number" id="originalAmount" value={originalAmount} onChange={e => setOriginalAmount(Number(e.target.value))} required min="0.01" step="0.01"
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>
      <div>
        <label htmlFor="outstandingAmount" className="block text-sm font-medium text-gray-700">Outstanding Amount</label>
        <input type="number" id="outstandingAmount" value={outstandingAmount} onChange={e => setOutstandingAmount(Number(e.target.value))} required min="0" step="0.01"
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>
      <div>
        <label htmlFor="dueDate" className="block text-sm font-medium text-gray-700">Due Date</label>
        <input type="date" id="dueDate" value={dueDate} onChange={e => setDueDate(e.target.value)} required
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

export default AccountsPayableForm;

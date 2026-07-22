import React, { useState, useEffect } from 'react';
import type { Customer, CustomerData } from '../services/customerService';

interface FormProps {
  onSubmit: (data: CustomerData) => void;
  onCancel: () => void;
  initialData?: Customer | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const CustomerForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [formData, setFormData] = useState<CustomerData>({
    fullName: '',
    email: '',
    phoneNumber: '',
    address: '',
    rfc: '',
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        fullName: initialData.fullName || '',
        email: initialData.email || '',
        phoneNumber: initialData.phoneNumber || '',
        address: initialData.address || '',
        rfc: initialData.rfc || '',
      });
    }
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isSubmitting) {
      onSubmit(formData);
    }
  };

  const inputStyle = "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {apiError && (
        <div className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700">
          <p className="font-bold">{apiError.message}</p>
        </div>
      )}
      <div>
        <label htmlFor="fullName" className="block text-sm font-bold text-gray-700">Full Name</label>
        <input id="fullName" name="fullName" value={formData.fullName} onChange={handleChange} required className={inputStyle} />
      </div>
      <div>
        <label htmlFor="email" className="block text-sm font-bold text-gray-700">Email</label>
        <input id="email" type="email" name="email" value={formData.email} onChange={handleChange} className={inputStyle} />
      </div>
      <div>
        <label htmlFor="phoneNumber" className="block text-sm font-bold text-gray-700">Phone Number</label>
        <input id="phoneNumber" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} className={inputStyle} />
      </div>
      <div>
        <label htmlFor="address" className="block text-sm font-bold text-gray-700">Address</label>
        <textarea id="address" name="address" value={formData.address} onChange={handleChange} className={inputStyle} />
      </div>
      <div>
        <label htmlFor="rfc" className="block text-sm font-bold text-gray-700">RFC</label>
        <input id="rfc" name="rfc" value={formData.rfc} onChange={handleChange} className={inputStyle} />
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded" disabled={isSubmitting}>
          Cancel
        </button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : 'Save'}
        </button>
      </div>
    </form>
  );
};

export default CustomerForm;

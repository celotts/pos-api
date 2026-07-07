import React, { useState, useEffect } from 'react';
import { Tax, TaxData } from '../services/taxService';

interface FormProps {
  onSubmit: (data: TaxData) => void;
  onCancel: () => void;
  initialData?: Tax | null;
  isSubmitting: boolean;
  error: string | null;
}

const TaxForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, error }) => {
  const [formData, setFormData] = useState<TaxData>({
    name: '', percentage: 0, taxType: 'IVA'
  });

  useEffect(() => {
    if (initialData) setFormData(initialData);
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: name === 'percentage' ? parseFloat(value) : value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isSubmitting) {
      onSubmit(formData);
    }
  };

  const inputStyle = "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline";
  const labelStyle = "block text-sm font-bold text-gray-700 mb-2";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && <p className="mb-4 text-center text-sm text-red-500">{error}</p>}
      <div>
        <label htmlFor="name" className={labelStyle}>Tax Name</label>
        <input id="name" name="name" value={formData.name} onChange={handleChange} required className={inputStyle} disabled={isSubmitting} />
      </div>
      <div>
        <label htmlFor="percentage" className={labelStyle}>Percentage (e.g., 0.16 for 16%)</label>
        <input id="percentage" type="number" name="percentage" value={formData.percentage} onChange={handleChange} required step="0.0001" className={inputStyle} disabled={isSubmitting} />
      </div>
      <div>
        <label htmlFor="taxType" className={labelStyle}>Tax Type</label>
        <select id="taxType" name="taxType" value={formData.taxType} onChange={handleChange} className={inputStyle} disabled={isSubmitting}>
          <option value="IVA">IVA</option>
          <option value="IEPS">IEPS</option>
          <option value="ISR">ISR</option>
        </select>
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded" disabled={isSubmitting}>
          Cancelar
        </button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
};

export default TaxForm;

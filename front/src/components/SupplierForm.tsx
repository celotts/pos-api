import React, { useState, useEffect } from 'react';
import { Supplier, SupplierData } from '../services/supplierService';

interface SupplierFormProps {
  onSubmit: (data: SupplierData) => void;
  onCancel: () => void;
  initialData?: Supplier | null;
}

const SupplierForm: React.FC<SupplierFormProps> = ({ onSubmit, onCancel, initialData }) => {
  const [formData, setFormData] = useState<SupplierData>({
    rfc: '', businessName: '', taxRegimen: '', contactEmail: ''
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        rfc: initialData.rfc,
        businessName: initialData.businessName,
        taxRegimen: initialData.taxRegimen,
        contactEmail: initialData.contactEmail,
      });
    }
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const inputStyle = "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline";
  const labelStyle = "block text-sm font-bold text-gray-700 mb-2";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="rfc" className={labelStyle}>RFC</label>
        <input id="rfc" name="rfc" value={formData.rfc} onChange={handleChange} required className={inputStyle} />
      </div>
      <div>
        <label htmlFor="businessName" className={labelStyle}>Business Name</label>
        <input id="businessName" name="businessName" value={formData.businessName} onChange={handleChange} required className={inputStyle} />
      </div>
      <div>
        <label htmlFor="taxRegimen" className={labelStyle}>Tax Regimen</label>
        <input id="taxRegimen" name="taxRegimen" value={formData.taxRegimen} onChange={handleChange} required className={inputStyle} />
      </div>
      <div>
        <label htmlFor="contactEmail" className={labelStyle}>Contact Email</label>
        <input id="contactEmail" type="email" name="contactEmail" value={formData.contactEmail} onChange={handleChange} required className={inputStyle} />
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">
          Cancelar
        </button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
          Guardar
        </button>
      </div>
    </form>
  );
};

export default SupplierForm;

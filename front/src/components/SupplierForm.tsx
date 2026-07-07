import React, { useState, useEffect } from 'react';
import { Supplier, SupplierData } from '../services/supplierService';

interface FormProps {
  onSubmit: (data: SupplierData) => void;
  onCancel: () => void;
  initialData?: Supplier | null;
  isSubmitting: boolean;
  error: string | null; // Error genérico del backend
  fieldErrors?: Record<string, string>; // Errores específicos por campo del backend
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const SupplierForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, error: apiError, fieldErrors: apiFieldErrors }) => {
  const [formData, setFormData] = useState<SupplierData>({
    rfc: '',
    businessName: '',
    taxRegimen: '',
    contactEmail: '',
  });

  const [errors, setErrors] = useState<Partial<Record<keyof SupplierData, string>>>({});

  useEffect(() => {
    if (initialData) {
      setFormData({
        rfc: initialData.rfc,
        businessName: initialData.businessName,
        taxRegimen: initialData.taxRegimen,
        contactEmail: initialData.contactEmail,
      });
    } else {
      setFormData({ rfc: '', businessName: '', taxRegimen: '', contactEmail: '' });
    }
    setErrors({});
  }, [initialData]);

  // Sincronizar errores del backend con los errores del frontend
  useEffect(() => {
    if (apiFieldErrors) {
      setErrors(prev => ({ ...prev, ...apiFieldErrors }));
    }
  }, [apiFieldErrors]);

  const validateField = (name: keyof SupplierData, value: string): string | null => {
    switch (name) {
      case 'rfc':
        if (!value) return 'El RFC es requerido.';
        if (value.length < 12 || value.length > 13) return 'El RFC debe tener 12 o 13 caracteres.';
        return null;
      case 'businessName':
        return !value ? 'La razón social es requerida.' : null;
      case 'taxRegimen':
        return !value ? 'El régimen fiscal es requerido.' : null;
      case 'contactEmail':
        if (!value) return 'El email de contacto es requerido.';
        if (!EMAIL_REGEX.test(value)) return 'El formato del email no es válido.';
        return null;
      default:
        return null;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target as { name: keyof SupplierData; value: string };
    setFormData(prev => ({ ...prev, [name]: value }));
    const error = validateField(name, value);
    setErrors(prev => ({ ...prev, [name]: error || undefined }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    let validationErrors: Partial<Record<keyof SupplierData, string>> = {};
    (Object.keys(formData) as Array<keyof SupplierData>).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) {
        validationErrors[key] = error;
      }
    });

    setErrors(validationErrors);

    if (Object.keys(validationErrors).length === 0 && !isSubmitting) {
      onSubmit(formData);
    }
  };

  const isSubmitDisabled = isSubmitting || Object.values(errors).some(e => e);

  return (
    <form onSubmit={handleSubmit} className="space-y-2">
      {apiError && !Object.keys(errors).length && <p className="mb-2 text-center text-sm text-red-500">{apiError}</p>}

      <div>
        <label htmlFor="rfc" className="block text-sm font-bold text-gray-700 mb-1">RFC</label>
        <input id="rfc" name="rfc" value={formData.rfc} onChange={handleChange} className={`w-full p-2 border rounded ${errors.rfc ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.rfc && <p className="text-xs text-red-500 mt-1">{errors.rfc}</p>}
      </div>
      <div>
        <label htmlFor="businessName" className="block text-sm font-bold text-gray-700 mb-1">Razón Social</label>
        <input id="businessName" name="businessName" value={formData.businessName} onChange={handleChange} className={`w-full p-2 border rounded ${errors.businessName ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.businessName && <p className="text-xs text-red-500 mt-1">{errors.businessName}</p>}
      </div>
      <div>
        <label htmlFor="taxRegimen" className="block text-sm font-bold text-gray-700 mb-1">Régimen Fiscal</label>
        <input id="taxRegimen" name="taxRegimen" value={formData.taxRegimen} onChange={handleChange} className={`w-full p-2 border rounded ${errors.taxRegimen ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.taxRegimen && <p className="text-xs text-red-500 mt-1">{errors.taxRegimen}</p>}
      </div>
      <div>
        <label htmlFor="contactEmail" className="block text-sm font-bold text-gray-700 mb-1">Email de Contacto</label>
        <input id="contactEmail" type="email" name="contactEmail" value={formData.contactEmail} onChange={handleChange} className={`w-full p-2 border rounded ${errors.contactEmail ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.contactEmail && <p className="text-xs text-red-500 mt-1">{errors.contactEmail}</p>}
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded" disabled={isSubmitting}>Cancelar</button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50" disabled={isSubmitDisabled}>
          {isSubmitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
};

export default SupplierForm;

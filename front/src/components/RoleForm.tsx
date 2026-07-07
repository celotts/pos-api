import React, { useState, useEffect, useMemo } from 'react';
import { Role } from '../services/roleService';

interface FormProps {
  onSubmit: (data: { name: string }) => void;
  onCancel: () => void;
  initialData?: Role | null;
  isSubmitting: boolean;
  error: string | null;
}

const RoleForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, error }) => {
  const [name, setName] = useState('');

  // Memoizamos los roles protegidos para evitar que se recalculen
  const protectedRoles = useMemo(() => ['ADMIN', 'USER'], []);

  // Estado para el error de validación del frontend
  const [validationError, setValidationError] = useState<string | null>(null);

  useEffect(() => {
    const newName = initialData?.name || '';
    setName(newName);
    validateName(newName); // Validar el nombre inicial
  }, [initialData]);

  const validateName = (value: string) => {
    if (protectedRoles.includes(value.toUpperCase()) && !initialData) {
      setValidationError("Cannot create a role with a reserved name.");
      return false;
    }
    setValidationError(null);
    return true;
  };

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newName = e.target.value;
    setName(newName);
    validateName(newName);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim() && !isSubmitting && !validationError) {
      onSubmit({ name: name.trim() });
    }
  };

  const isSubmitDisabled = isSubmitting || !!validationError;

  return (
    <form onSubmit={handleSubmit}>
      {/* Mostramos el error del backend (API) o el de validación del frontend */}
      {(error || validationError) && (
        <p className="mb-4 text-center text-sm text-red-500">{error || validationError}</p>
      )}
      <div className="mb-4">
        <label htmlFor="name" className="block text-sm font-bold text-gray-700 mb-2">
          Role Name
        </label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={handleNameChange}
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${validationError ? 'border-red-500' : ''}`}
          required
          disabled={isSubmitting}
        />
      </div>
      <div className="flex justify-end space-x-4 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded" disabled={isSubmitting}>
          Cancelar
        </button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50" disabled={isSubmitDisabled}>
          {isSubmitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
};

export default RoleForm;

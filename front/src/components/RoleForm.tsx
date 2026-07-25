import React, { useState, useEffect, useMemo } from 'react';
import type { Role, RoleData } from '../services/roleService';

interface FormProps {
  onSubmit: (data: RoleData) => void;
  onCancel: () => void;
  initialData?: Role | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const RoleForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [name, setName] = useState('');
  const [validationError, setValidationError] = useState<string | null>(null);

  const protectedRoles = useMemo(() => ['ADMIN', 'USER'], []);

  useEffect(() => {
    const newName = initialData?.name || '';
    setName(newName);
    if (initialData) { // Only validate for existing roles if they are protected
        setValidationError(null); // Clear validation for edits
    } else {
        validateName(newName);
    }
  }, [initialData]);

  const validateName = (value: string) => {
    if (protectedRoles.includes(value.toUpperCase())) {
      setValidationError("Cannot create or modify to a reserved role name.");
      return false;
    }
    setValidationError(null);
    return true;
  };

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newName = e.target.value;
    setName(newName);
    if (!initialData) { // Only validate on create
        validateName(newName);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim() && !isSubmitting && !validationError) {
      onSubmit({ name: name.trim() });
    }
  };
  
  const isSubmitDisabled = isSubmitting || !!validationError || (initialData && protectedRoles.includes(initialData.name.toUpperCase()));

  return (
    <form onSubmit={handleSubmit}>
      {apiError && (
        <div className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700">
          <p className="font-bold">{apiError.message}</p>
        </div>
      )}
       {validationError && <p className="mb-4 text-center text-sm text-red-500">{validationError}</p>}

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
          disabled={isSubmitting || (initialData && protectedRoles.includes(initialData.name.toUpperCase()))}
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

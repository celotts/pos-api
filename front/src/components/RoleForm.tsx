import React, { useState, useEffect } from 'react';
import { Role } from '../services/roleService';

interface RoleFormProps {
  onSubmit: (data: { name: string }) => void;
  onCancel: () => void;
  initialData?: Role | null;
}

const RoleForm: React.FC<RoleFormProps> = ({ onSubmit, onCancel, initialData }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    setName(initialData?.name || '');
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim()) {
      onSubmit({ name });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="mb-4">
        <label htmlFor="name" className="block text-sm font-bold text-gray-700 mb-2">
          Role Name
        </label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          required
        />
      </div>
      <div className="flex justify-end space-x-4 pt-4">
        <button
          type="button"
          onClick={onCancel}
          className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
        >
          Cancelar
        </button>
        <button
          type="submit"
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Guardar
        </button>
      </div>
    </form>
  );
};

export default RoleForm;

import React, { useState, useEffect } from 'react';
import { Category } from '../services/categoryService';

interface FormProps {
  onSubmit: (data: { name: string }) => void; // <-- Cambiado para esperar un objeto
  onCancel: () => void;
  initialData?: Category | null;
  isSubmitting: boolean;
  error: string | null;
}

const CategoryForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, error }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    setName(initialData?.name || '');
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedName = name.trim();
    if (trimmedName && !isSubmitting) {
      // Enviamos el objeto que la API espera, no solo el texto.
      onSubmit({ name: trimmedName });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <p className="mb-4 text-center text-sm text-red-500">{error}</p>}
      <div className="mb-4">
        <label htmlFor="name" className="block text-sm font-bold text-gray-700 mb-2">
          Category Name
        </label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          required
          disabled={isSubmitting}
        />
      </div>
      <div className="flex justify-end space-x-4 pt-4">
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

export default CategoryForm;

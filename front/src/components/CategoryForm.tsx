import React, { useState, useEffect } from 'react';
import { Category } from '../services/categoryService';

interface CategoryFormProps {
  onSubmit: (name: string) => void;
  onCancel: () => void;
  initialData?: Category | null;
  isSubmitting: boolean;
  error: string | null;
}

const CategoryForm: React.FC<CategoryFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, error }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    if (initialData) {
      setName(initialData.name);
    } else {
      setName('');
    }
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name.trim() && !isSubmitting) {
      onSubmit(name.trim());
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
      <div className="flex justify-end space-x-4">
        <button
          type="button"
          onClick={onCancel}
          className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
          disabled={isSubmitting}
        >
          Cancelar
        </button>
        <button
          type="submit"
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
};

export default CategoryForm;

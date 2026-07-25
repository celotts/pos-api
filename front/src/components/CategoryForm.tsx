import React, { useState, useEffect } from 'react';
import type { Category, CategoryData } from '../services/categoryService';

interface FormProps {
  onSubmit: (data: CategoryData) => void;
  onCancel: () => void;
  initialData?: Category | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const CategoryForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    setName(initialData?.name || '');
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedName = name.trim();
    if (trimmedName && !isSubmitting) {
      onSubmit({ name: trimmedName });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {apiError && (
        <div className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700">
          <p className="font-bold">{apiError.message}</p>
          {apiError.errors && (
            <ul className="ml-5 mt-2 list-disc">
              {apiError.errors.map((err, index) => (
                <li key={index}>{err}</li>
              ))}
            </ul>
          )}
        </div>
      )}
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

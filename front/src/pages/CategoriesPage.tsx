import React, { useEffect, useState } from 'react';
import { categoryService, Category } from '../services/categoryService';
import { useAuth } from '../hooks/useAuth';
import Modal from '../components/Modal';

// El formulario ahora es un componente interno para simplificar
const CategoryForm: React.FC<{
  onSubmit: (name: string) => void;
  onCancel: () => void;
  initialData?: Category | null;
  isSubmitting: boolean;
  error: string | null;
}> = ({ onSubmit, onCancel, initialData, isSubmitting, error }) => {
  const [name, setName] = useState('');

  useEffect(() => {
    setName(initialData?.name || '');
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


const CategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [pageError, setPageError] = useState<string | null>(null);
  const { isAdmin, isEditor } = useAuth();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  const [formIsSubmitting, setFormIsSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const data = await categoryService.getAll();
      setCategories(data);
      setPageError(null);
    } catch (err) {
      setPageError('Failed to fetch categories.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleOpenModal = (category: Category | null = null) => {
    setEditingCategory(category);
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingCategory(null);
  };

  const handleFormSubmit = async (name: string) => {
    setFormIsSubmitting(true);
    setFormError(null);
    try {
      if (editingCategory) {
        await categoryService.update(editingCategory.id, { name });
      } else {
        await categoryService.create({ name });
      }
      handleCloseModal();
      fetchCategories();
    } catch (err: any) {
      const message = err.response?.data?.message || 'An unexpected error occurred.';
      setFormError(message);
    } finally {
      setFormIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await categoryService.delete(id);
        fetchCategories();
      } catch (err) {
        console.error('Failed to delete category', err);
      }
    }
  };

  if (loading) return <div className="text-center p-4">Loading...</div>;
  if (pageError) return <div className="text-center p-4 text-red-500">{pageError}</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Manage Categories</h1>
        {(isAdmin || isEditor) && (
          <button onClick={() => handleOpenModal()} className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            + New Category
          </button>
        )}
      </div>

      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Name</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Created By</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Last Updated By</th>
              {(isAdmin || isEditor) && <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100"></th>}
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{category.name}</td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{category.createdByName || 'N/A'}</td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{category.updatedByName || 'N/A'}</td>
                {(isAdmin || isEditor) && (
                  <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm text-right">
                    <button onClick={() => handleOpenModal(category)} className="text-indigo-600 hover:text-indigo-900 mr-4">Edit</button>
                    {isAdmin && (
                      <button onClick={() => handleDelete(category.id)} className="text-red-600 hover:text-red-900">Delete</button>
                    )}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={isModalOpen} onClose={handleCloseModal} title={editingCategory ? 'Edit Category' : 'Create New Category'}>
        <CategoryForm
          onSubmit={handleFormSubmit}
          onCancel={handleCloseModal}
          initialData={editingCategory}
          isSubmitting={formIsSubmitting}
          error={formError}
        />
      </Modal>
    </div>
  );
};

export default CategoriesPage;

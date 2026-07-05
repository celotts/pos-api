import React, { useEffect, useState } from 'react';
import categoryService, { Category } from '../services/categoryService';
import { useAuth } from '../hooks/useAuth';
import Modal from '../components/Modal';
import CategoryForm from '../components/CategoryForm';

const CategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isAdmin, isEditor } = useAuth();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const data = await categoryService.getAllCategories();
      setCategories(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch categories.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleOpenModal = (category: Category | null = null) => {
    setEditingCategory(category);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingCategory(null);
  };

  const handleFormSubmit = async (name: string) => {
    try {
      if (editingCategory) {
        await categoryService.updateCategory(editingCategory.id, { name });
      } else {
        await categoryService.createCategory({ name });
      }
      handleCloseModal();
      fetchCategories();
    } catch (err) {
      console.error('Failed to save category', err);
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await categoryService.deleteCategory(id);
        fetchCategories();
      } catch (err) {
        console.error('Failed to delete category', err);
      }
    }
  };

  if (loading) return <div className="text-center p-4">Loading...</div>;
  if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

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

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editingCategory ? 'Edit Category' : 'Create Category'}
      >
        <CategoryForm
          onSubmit={handleFormSubmit}
          onCancel={handleCloseModal}
          initialData={editingCategory}
        />
      </Modal>
    </div>
  );
};

export default CategoriesPage;

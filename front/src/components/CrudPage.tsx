import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import Modal from './Modal';
import Button from './ui/Button';
import Table from './ui/Table';

// La interfaz de props para los formularios ahora incluye una lista de errores
interface FormProps<T> {
  onSubmit: (data: any) => void;
  onCancel: () => void;
  initialData?: T | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

interface CrudPageProps<T extends { id: string }> {
  title: string;
  service: {
    getAll: () => Promise<T[]>;
    create: (data: any) => Promise<T>;
    update: (id: string, data: any) => Promise<T>;
    delete: (id: string) => Promise<void>;
  };
  columns: { header: string; accessor: keyof T; render?: (item: T) => React.ReactNode }[];
  form: React.FC<FormProps<T>>;
  isActionsAllowed?: (item: T) => boolean;
}

const CrudPage = <T extends { id: string }>({ title, service, columns, form: FormComponent, isActionsAllowed }: CrudPageProps<T>) => {
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const { isAdmin, isEditor } = useAuth();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<T | null>(null);

  const [formIsSubmitting, setFormIsSubmitting] = useState(false);
  const [apiError, setApiError] = useState<{ message: string; errors?: string[] } | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const data = await service.getAll();
      if (Array.isArray(data)) {
        setItems(data);
      } else {
        console.error(`Data fetched for "${title}" is not an array. Received:`, data);
        setItems([]);
      }
    } catch (error) {
      console.error(`Failed to fetch ${title}`, error);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleOpenModal = (item: T | null = null) => {
    setEditingItem(item);
    setApiError(null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    if (formIsSubmitting) return;
    setIsModalOpen(false);
    setEditingItem(null);
  };

  const handleFormSubmit = async (data: any) => {
    setFormIsSubmitting(true);
    setApiError(null);
    try {
      if (editingItem) {
        await service.update(editingItem.id, data);
      } else {
        await service.create(data);
      }
      handleCloseModal();
      fetchData();
    } catch (err: any) {
      // Extraemos la respuesta de error estructurada del backend
      const errorData = err.response?.data || { message: 'An unexpected error occurred.' };
      setApiError(errorData);
    } finally {
      setFormIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure?')) {
      try {
        await service.delete(id);
        fetchData();
      } catch (error) {
        console.error(`Failed to delete ${title}`, error);
      }
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">{title}</h1>
        {(isAdmin || isEditor) && (
          <Button onClick={() => handleOpenModal()}>+ New {title.slice(0, -1)}</Button>
        )}
      </div>
      <Table<T>
        columns={columns}
        data={items}
        renderActions={(item) => {
          if (isActionsAllowed && !isActionsAllowed(item)) {
            return null;
          }
          return (
            <div className="space-x-2">
              {(isAdmin || isEditor) && <Button variant="secondary" onClick={() => handleOpenModal(item)}>Edit</Button>}
              {isAdmin && <Button variant="danger" onClick={() => handleDelete(item.id)}>Delete</Button>}
            </div>
          );
        }}
      />
      <Modal isOpen={isModalOpen} onClose={handleCloseModal} title={editingItem ? `Edit ${title.slice(0, -1)}` : `New ${title.slice(0, -1)}`}>
        <FormComponent
          onSubmit={handleFormSubmit}
          onCancel={handleCloseModal}
          initialData={editingItem}
          isSubmitting={formIsSubmitting}
          apiError={apiError}
        />
      </Modal>
    </div>
  );
};

export default CrudPage;

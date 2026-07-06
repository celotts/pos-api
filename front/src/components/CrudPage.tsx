import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import Modal from './Modal';
import Button from './ui/Button';
import Table from './ui/Table';

interface CrudPageProps<T extends { id: string }> {
  title: string;
  service: {
    getAll: () => Promise<T[]>;
    create: (data: any) => Promise<T>;
    update: (id: string, data: any) => Promise<T>;
    delete: (id: string) => Promise<void>;
  };
  columns: { header: string; accessor: keyof T }[];
  form: React.FC<{ onSubmit: (data: any) => void; onCancel: () => void; initialData?: T | null }>;
}

const CrudPage = <T extends { id: string }>({ title, service, columns, form: FormComponent }: CrudPageProps<T>) => {
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const { isAdmin, isEditor } = useAuth();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<T | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const data = await service.getAll();
      setItems(data);
    } catch (error) {
      console.error(`Failed to fetch ${title}`, error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleOpenModal = (item: T | null = null) => {
    setEditingItem(item);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingItem(null);
  };

  const handleFormSubmit = async (data: any) => {
    try {
      if (editingItem) {
        await service.update(editingItem.id, data);
      } else {
        await service.create(data);
      }
      handleCloseModal();
      fetchData();
    } catch (error) {
      console.error(`Failed to save ${title}`, error);
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
        renderActions={(item) => (
          <div className="space-x-2">
            {(isAdmin || isEditor) && <Button variant="secondary" onClick={() => handleOpenModal(item)}>Edit</Button>}
            {isAdmin && <Button variant="danger" onClick={() => handleDelete(item.id)}>Delete</Button>}
          </div>
        )}
      />
      <Modal isOpen={isModalOpen} onClose={handleCloseModal} title={editingItem ? `Edit ${title.slice(0, -1)}` : `New ${title.slice(0, -1)}`}>
        <FormComponent onSubmit={handleFormSubmit} onCancel={handleCloseModal} initialData={editingItem} />
      </Modal>
    </div>
  );
};

export default CrudPage;

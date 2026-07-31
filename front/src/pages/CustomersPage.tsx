import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch } from '../store/store';
import {
  fetchCustomers,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  selectAllCustomers,
  selectCustomersLoading,
  selectCustomersError,
} from '../store/slices/customerSlice';
import { Customer, CustomerPayload } from '../models/customer.model';
import CustomerList from '../components/customers/CustomerList';
import CustomerForm from '../components/customers/CustomerForm';
import Modal from '../components/ui/Modal';
import Button from '../components/ui/Button';

const CustomersPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const customers = useSelector(selectAllCustomers);
  const loading = useSelector(selectCustomersLoading);
  const error = useSelector(selectCustomersError);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [customerToEdit, setCustomerToEdit] = useState<Customer | null>(null);

  useEffect(() => {
    dispatch(fetchCustomers());
  }, [dispatch]);

  const handleAddNew = () => {
    setCustomerToEdit(null);
    setIsModalOpen(true);
  };

  const handleEdit = (customer: Customer) => {
    setCustomerToEdit(customer);
    setIsModalOpen(true);
  };

  const handleDelete = (id: string) => {
    if (window.confirm('Are you sure you want to delete this customer?')) {
      dispatch(deleteCustomer(id));
    }
  };

  const handleSave = (customerData: CustomerPayload) => {
    if (customerToEdit) {
      dispatch(updateCustomer({ id: customerToEdit.id, customer: customerData }));
    } else {
      dispatch(createCustomer(customerData));
    }
    setIsModalOpen(false);
  };

  return (
    <div className="container mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Customer Management</h1>
        <Button onClick={handleAddNew}>
          Add New Customer
        </Button>
      </div>

      {loading === 'pending' && <p>Loading...</p>}
      {error && <p className="text-red-500">Error: {error}</p>}

      <CustomerList customers={customers} onEdit={handleEdit} onDelete={handleDelete} />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2 className="text-2xl font-bold mb-4">
          {customerToEdit ? 'Edit Customer' : 'Add New Customer'}
        </h2>
        <CustomerForm
          customerToEdit={customerToEdit}
          onSave={handleSave}
          onCancel={() => setIsModalOpen(false)}
        />
      </Modal>
    </div>
  );
};

export default CustomersPage;

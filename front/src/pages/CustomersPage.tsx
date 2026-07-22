import React from 'react';
import CrudPage from '../components/CrudPage';
import { customerService } from '../services/customerService';
import CustomerForm from '../components/CustomerForm';

const CustomersPage: React.FC = () => {
  const columns = [
    { header: 'Full Name', accessor: 'fullName' as const },
    { header: 'Email', accessor: 'email' as const },
    { header: 'Phone', accessor: 'phoneNumber' as const },
    { header: 'RFC', accessor: 'rfc' as const },
  ];

  return (
    <CrudPage
      title="Customers"
      service={customerService}
      columns={columns}
      form={CustomerForm}
    />
  );
};

export default CustomersPage;

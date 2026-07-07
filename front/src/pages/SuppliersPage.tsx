import React from 'react';
import CrudPage from '../components/CrudPage';
import { supplierService } from '../services/supplierService';
import SupplierForm from '../components/SupplierForm';

const SuppliersPage: React.FC = () => {
  const columns = [
    { header: 'RFC', accessor: 'rfc' as const },
    { header: 'Business Name', accessor: 'businessName' as const },
    { header: 'Email', accessor: 'contactEmail' as const },
  ];

  return (
    <CrudPage
      title="Suppliers"
      service={supplierService}
      columns={columns}
      form={SupplierForm}
    />
  );
};

export default SuppliersPage;

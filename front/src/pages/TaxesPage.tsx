import React from 'react';
import CrudPage from '../components/CrudPage';
import { taxService } from '../services/taxService';
import TaxForm from '../components/TaxForm';

const TaxesPage: React.FC = () => {
  const columns = [
    { header: 'Name', accessor: 'name' as const },
    { header: 'Percentage', accessor: 'percentage' as const },
    { header: 'Type', accessor: 'taxType' as const },
  ];

  return (
    <CrudPage
      title="Taxes"
      service={taxService}
      columns={columns}
      form={TaxForm}
    />
  );
};

export default TaxesPage;

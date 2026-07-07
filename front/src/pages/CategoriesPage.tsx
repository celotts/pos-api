import React from 'react';
import CrudPage from '../components/CrudPage';
import { categoryService } from '../services/categoryService';
import CategoryForm from '../components/CategoryForm';

const CategoriesPage: React.FC = () => {
  const columns = [
    { header: 'Name', accessor: 'name' as const },
    { header: 'Created By', accessor: 'createdByName' as const },
    { header: 'Updated By', accessor: 'updatedByName' as const },
  ];

  return (
    <CrudPage
      title="Categories"
      service={categoryService}
      columns={columns}
      form={CategoryForm}
    />
  );
};

export default CategoriesPage;

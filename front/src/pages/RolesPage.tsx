import React from 'react';
import CrudPage from '../components/CrudPage';
import { roleService } from '../services/roleService';
import RoleForm from '../components/RoleForm';

const RolesPage: React.FC = () => {
  const columns = [
    { header: 'Name', accessor: 'name' as const },
    { header: 'Created By', accessor: 'createdByName' as const },
    { header: 'Updated By', accessor: 'updatedByName' as const },
  ];

  return (
    <CrudPage
      title="Roles"
      service={roleService}
      columns={columns}
      form={RoleForm}
    />
  );
};

export default RolesPage;

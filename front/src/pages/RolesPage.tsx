import React from 'react';
import CrudPage from '../components/CrudPage';
import { roleService, Role } from '../services/roleService';
import RoleForm from '../components/RoleForm';

const RolesPage: React.FC = () => {
  const columns = [
    { header: 'Name', accessor: 'name' as const },
    { header: 'Created By', accessor: 'createdByName' as const },
    { header: 'Updated By', accessor: 'updatedByName' as const },
  ];

  // La lógica de negocio para deshabilitar acciones en ciertos roles.
  const isActionsAllowed = (role: Role) => {
    const protectedRoles = ['ADMIN', 'USER'];
    return !protectedRoles.includes(role.name.toUpperCase());
  };

  return (
    <CrudPage<Role>
      title="Roles"
      service={roleService}
      columns={columns}
      form={RoleForm}
      isActionsAllowed={isActionsAllowed} // <-- Pasamos la función de lógica
    />
  );
};

export default RolesPage;

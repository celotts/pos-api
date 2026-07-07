import React, { useEffect, useState } from 'react';
import CrudPage from '../components/CrudPage';
import { userService, User } from '../services/userService';
import { roleService, Role } from '../services/roleService';
import UserForm from '../components/UserForm';

const UsersPage: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loadingRoles, setLoadingRoles] = useState(true);

  useEffect(() => {
    roleService.getAll()
      .then(setRoles)
      .catch(err => console.error("Failed to load roles", err))
      .finally(() => setLoadingRoles(false));
  }, []);

  const columns = [
    { header: 'Full Name', accessor: 'fullName' as const },
    { header: 'Email', accessor: 'email' as const },
    {
      header: 'Role',
      accessor: 'role' as const,
      // CÓDIGO DEFENSIVO: Si user.role no existe, muestra 'N/A' en lugar de romper la app.
      render: (user: User) => user.role ? user.role.name : 'N/A'
    },
    {
      header: 'Active',
      accessor: 'isActive' as const,
      render: (user: User) => (
        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
          user.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
        }`}>
          {user.isActive ? 'Yes' : 'No'}
        </span>
      )
    },
  ];

  if (loadingRoles) {
    return <div className="text-center p-4">Loading component data...</div>;
  }

  return (
    <CrudPage<User>
      title="Users"
      service={userService}
      columns={columns}
      form={(props) => <UserForm {...props} roles={roles} />}
    />
  );
};

export default UsersPage;

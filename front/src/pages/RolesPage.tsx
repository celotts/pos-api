import React, { useEffect, useState } from 'react';
import roleService, { Role } from '../services/roleService'; // Asumiendo que crearás este servicio
import { useAuth } from '../hooks/useAuth';
import Modal from '../components/Modal';
// Asumiendo que crearás un RoleForm similar a CategoryForm

const RolesPage: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const { isAdmin } = useAuth();

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        setLoading(true);
        // const data = await roleService.getAllRoles();
        // setRoles(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    // fetchRoles();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <h1 className="text-2xl font-bold">Manage Roles</h1>
      {/* El resto del CRUD de Roles iría aquí */}
    </div>
  );
};

export default RolesPage;

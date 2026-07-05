import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ROLES } from '../utils/roles';

const Sidebar: React.FC = () => {
  const { isAdmin } = useAuth();

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `block px-4 py-2 rounded-lg text-gray-300 hover:bg-gray-700 ${
      isActive ? 'bg-gray-700 text-white' : ''
    }`;

  return (
    <aside className="w-64 bg-gray-800 text-white flex-shrink-0">
      <div className="p-4">
        <h1 className="text-2xl font-bold text-white text-center">POS-API</h1>
      </div>
      <nav className="mt-8 p-2 flex flex-col space-y-2">
        <NavLink to="/" end className={linkClass}>
          Dashboard
        </NavLink>

        {isAdmin && (
          <NavLink to="/categories" className={linkClass}>
            Categories
          </NavLink>
        )}
      </nav>
    </aside>
  );
};

export default Sidebar;

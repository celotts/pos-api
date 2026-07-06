import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar: React.FC = () => {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `block px-4 py-2 rounded-lg text-gray-300 hover:bg-gray-700 ${
      isActive ? 'bg-gray-700 text-white' : ''
    }`;

  return (
    // Las clases w-64 (ancho fijo) y flex-shrink-0 (no encoger) son cruciales.
    <aside className="w-64 bg-gray-800 text-white flex-shrink-0">
      <div className="p-4">
        <h1 className="text-2xl font-bold text-white text-center">POS-API</h1>
      </div>
      <nav className="mt-8 p-2 flex flex-col space-y-2">
        <NavLink to="/" end className={linkClass}>
          Dashboard
        </NavLink>
        <NavLink to="/categories" className={linkClass}>
          Categories
        </NavLink>
        {/* Aquí añadiremos más enlaces después */}
      </nav>
    </aside>
  );
};

export default Sidebar;

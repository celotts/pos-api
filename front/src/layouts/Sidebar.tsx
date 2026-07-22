import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ROLES } from '../utils/roles';

const Sidebar: React.FC = () => {
  const { isAdmin, isEditor, isViewer } = useAuth();
  const [isMaintenanceOpen, setIsMaintenanceOpen] = useState(false);

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

        <NavLink to="/products" className={linkClass}>
          Products
        </NavLink>

        <NavLink to="/purchases" className={linkClass}>
          Purchases
        </NavLink>

        {isAdmin && (
          <div>
            <button
              onClick={() => setIsMaintenanceOpen(!isMaintenanceOpen)}
              className="w-full flex justify-between items-center px-4 py-2 rounded-lg text-gray-300 hover:bg-gray-700"
            >
              <span>Mantenimiento</span>
              <svg
                className={`w-5 h-5 transition-transform ${isMaintenanceOpen ? 'rotate-180' : ''}`}
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            {isMaintenanceOpen && (
              <div className="pl-4 mt-2 space-y-2">
                <NavLink to="/users" className={linkClass}>Users</NavLink>
                <NavLink to="/roles" className={linkClass}>Roles</NavLink>
                <NavLink to="/categories" className={linkClass}>Categories</NavLink>
                <NavLink to="/suppliers" className={linkClass}>Suppliers</NavLink>
                <NavLink to="/taxes" className={linkClass}>Taxes</NavLink>
                <NavLink to="/shifts" className={linkClass}>Shifts</NavLink>
                <NavLink to="/accounts-payable" className={linkClass}>Accounts Payable</NavLink>
                <NavLink to="/cash-accounts" className={linkClass}>Cash Accounts</NavLink>
              </div>
            )}
          </div>
        )}
      </nav>
    </aside>
  );
};

export default Sidebar;

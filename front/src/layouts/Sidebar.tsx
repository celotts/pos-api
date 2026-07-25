import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ROLES } from '../utils/roles';

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { isAdmin, isEditor } = useAuth();
  const [isMaintenanceOpen, setIsMaintenanceOpen] = useState(false);

  const handleLinkClick = () => {
    onClose();
  };
  
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `block px-4 py-2 rounded-lg text-secondary-300 hover:bg-secondary-700 ${
      isActive ? 'bg-secondary-900 text-white' : ''
    }`;

  const sidebarClasses = `
    fixed inset-y-0 left-0 z-30 w-64 bg-secondary-800 text-white transform 
    transition-transform duration-300 ease-in-out
    lg:relative lg:translate-x-0 lg:flex-shrink-0
    ${isOpen ? 'translate-x-0' : '-translate-x-full'}
  `;

  const canShowMaintenance = isAdmin || isEditor;

  return (
    <>
        <aside className={sidebarClasses}>
            <div className="flex items-center justify-between p-4">
                <h1 className="text-2xl font-bold text-white text-center">POS-API</h1>
                <button onClick={onClose} className="lg:hidden">
                    <svg className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>
            <nav className="mt-8 p-2 flex flex-col space-y-2">
                <NavLink to="/" end className={linkClass} onClick={handleLinkClick}>
                Dashboard
                </NavLink>

                <NavLink to="/products" className={linkClass} onClick={handleLinkClick}>
                Products
                </NavLink>

                <NavLink to="/purchases" className={linkClass} onClick={handleLinkClick}>
                Purchases
                </NavLink>

                {canShowMaintenance && (
                <div>
                    <button
                    onClick={() => setIsMaintenanceOpen(!isMaintenanceOpen)}
                    className="w-full flex justify-between items-center px-4 py-2 rounded-lg text-secondary-300 hover:bg-secondary-700"
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
                        {(isAdmin || isEditor) && (
                          <>
                            <NavLink to="/categories" className={linkClass} onClick={handleLinkClick}>Categories</NavLink>
                            <NavLink to="/suppliers" className={linkClass} onClick={handleLinkClick}>Suppliers</NavLink>
                            <NavLink to="/taxes" className={linkClass} onClick={handleLinkClick}>Taxes</NavLink>
                            <NavLink to="/customers" className={linkClass} onClick={handleLinkClick}>Customers</NavLink>
                          </>
                        )}
                        {isAdmin && (
                          <>
                            <NavLink to="/users" className={linkClass} onClick={handleLinkClick}>Users</NavLink>
                            <NavLink to="/roles" className={linkClass} onClick={handleLinkClick}>Roles</NavLink>
                            <NavLink to="/shifts" className={linkClass} onClick={handleLinkClick}>Shifts</NavLink>
                            <NavLink to="/accounts-payable" className={linkClass} onClick={handleLinkClick}>Accounts Payable</NavLink>
                            <NavLink to="/cash-accounts" className={linkClass} onClick={handleLinkClick}>Cash Accounts</NavLink>
                          </>
                        )}
                    </div>
                    )}
                </div>
                )}
            </nav>
        </aside>
        {isOpen && <div className="fixed inset-0 bg-black opacity-50 z-20 lg:hidden" onClick={onClose}></div>}
    </>
  );
};

export default Sidebar;
